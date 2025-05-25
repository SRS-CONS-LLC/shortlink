const {createApp, ref, reactive, computed, watch, onMounted} = Vue;

createApp({
    setup() {

        const baseUrl = computed(() => {
            // Get the current origin (protocol + hostname + port)
            return window.location.origin;
        });
        // User information
        const loggedInUser = reactive({
            name: 'John',
            surname: 'Doe',
            picture: 'https://via.placeholder.com/32'
        });

        // Sidebar links
        const links = ref([]);
        const selectedLinkId = ref(null);

        // Tabs configuration
        const tabs = [
            {id: 'link-in-bio', name: 'Link-in-Bio'},
        ];
        const activeTab = ref('app-shortener');

        // App shortener data
        const appShortener = reactive({
            ios: '',
            android: '',
            windows: '',
            macos: '',
            ipad: '',
            appGallery: '',
            amazon: ''
        });

        // URL shortener data
        const urlShortener = reactive({
            url: 'https://CITOUT.me',
            passwordProtected: false
        });

        // Link-in-Bio data
        const layouts = [
            {name: 'List', value: 'list'},
            {name: 'Grid', value: 'grid'}
        ];

        const themes = [
            {name: 'Light', value: 'light'},
            {name: 'Auto', value: 'auto'},
            {name: 'Dark', value: 'dark'}
        ];

        const linkInBio = reactive({
            title: 'MyTitle',
            description: 'CITOUT',
            logoFile: null,
            logoPreview: null,
            logoUrl: null,
            removeMainLogo: false,
            themeColor: '#101223',
            layoutType: 'list',
            themeType: 'dark',
            linkType: 'REDIRECT',
            shortCode: '',
            shortUrl:'',
            originalUrl:'',
            links: [
                {title: 'Title', url: '', logoFile: null, logoPreview: null, logoUrl: null, removeLogo: false},
                {title: 'Title', url: '', logoFile: null, logoPreview: null, logoUrl: null, removeLogo: false},
                {title: 'Title', url: '', logoFile: null, logoPreview: null, logoUrl: null, removeLogo: false}
            ]
        });

        // QR Tag data
        const qrTag = reactive({
            title: 'CITOUT Link',
            description: 'CITOUT',
            imageFile: null,
            imagePreview: null,
            themeColor: '#000000',
            preset: 'Select a preset'
        });

        // Status flags
        const isSaving = ref(false);
        const alerts = reactive({
            success: {show: false, message: 'Link in bio saved successfully!'},
            error: {show: false, message: ''}
        });

        // Methods
        async function loadLinks() {
            try {
                const response = await fetch('/api/short-links');
                if (!response.ok) {
                    if (response.status === 401 || response.status === 403) {
                        // Redirect to login page if unauthorized
                        window.location.href = '/';
                        return;
                    }
                    throw new Error('Failed to load links');
                }
                links.value = await response.json();
                checkUrlParameters();
            } catch (error) {
                console.error('Error loading links:', error);
                // Show error message to user
                alerts.error.show = true;
                alerts.error.message = 'Failed to load links. Please try again.';
                setTimeout(() => {
                    alerts.error.show = false;
                }, 5000);
            }
        }

        function selectLink(linkId) {
            selectedLinkId.value = linkId;
            activeTab.value = 'link-in-bio';

            // Update URL with both tab and id parameters
            const newUrl = new URL(window.location.href);
            newUrl.searchParams.set('tab', 'link-in-bio');
            newUrl.searchParams.set('id', linkId);
            window.history.pushState({}, '', newUrl);

            // Load link details
            loadLinkDetails(linkId);
        }


        function addLink() {
            linkInBio.links.push({
                title: 'New Link',
                url: '',
                logoFile: null,
                logoPreview: null,
                logoUrl: null
            });
        }

        function softDeleteLink(index) {
            linkInBio.links[index].deleted = true;
        }

        function handleQRTagImageUpload(event) {
            const file = event.target.files[0];
            if (file) {
                qrTag.imageFile = file;
                const reader = new FileReader();
                reader.onload = (e) => {
                    qrTag.imagePreview = e.target.result;
                };
                reader.readAsDataURL(file);
            }
        }

        async function createNewLink() {
            try {
                // Create empty link in database
                const formData = new FormData();
                formData.append('title', 'New Link');
                formData.append('description', 'CITOUT');
                formData.append('themeColor', '#000000');
                formData.append('themeType', 'AUTO');
                formData.append('layoutType', 'LIST');
                formData.append('linkType', null);

                // Add one empty link
                formData.append('links[0].title', 'New Link');
                formData.append('links[0].url', '');

                // Send to API
                const response = await fetch('/api/short-links', {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    throw new Error('Failed to create new link');
                }

                const newLink = await response.json();

                // Update URL with the new ID
                const newUrl = new URL(window.location.href);
                newUrl.searchParams.set('tab', 'link-in-bio');
                newUrl.searchParams.set('id', newLink.id);
                window.history.pushState({}, '', newUrl);

                // Update UI
                await loadLinks();
                activeTab.value = 'link-in-bio';
                selectedLinkId.value = newLink.id;

                // Load the new link details
                await loadLinkDetails(newLink.id);
            } catch (error) {
                console.error('Error creating new link:', error);
            }
        }

        // Check URL parameters on page load
        function checkUrlParameters() {
            const url = new URL(window.location.href);
            console.log('checkUrlParameters='+url);
            var tabParam = url.searchParams.get('tab');
            var idParam = url.searchParams.get('id');
            console.log('checkUrlParameters idparam='+idParam);
            console.log(JSON.stringify(links.value));
            if (!idParam) {
                if(links.value.length>0) {
                    idParam = links.value[0].id;
                }
            }

            if (!tabParam) {
                tabParam = 'link-in-bio';
            }
            console.log('checkUrlParameters idparam2='+idParam);

            if (idParam) {
                selectLink(idParam);
            }

            activeTab.value = tabParam;
        }


// Updated handleLogoUpload function
        function handleLogoUpload(event) {
            const file = event.target.files[0];
            if (file) {
                linkInBio.logoFile = file;
                const reader = new FileReader();
                reader.onload = (e) => {
                    linkInBio.logoPreview = e.target.result;
                };
                reader.readAsDataURL(file);

                // Clear the existing logoUrl and reset remove flag
                linkInBio.logoUrl = null;
                linkInBio.removeMainLogo = false;
            }
        }

// Updated deleteLogo function with no immediate DELETE request
        function deleteLogo() {
            // Flag that the logo should be removed when saving
            linkInBio.removeMainLogo = true;

            // Clear logo data for UI updates
            linkInBio.logoFile = null;
            linkInBio.logoPreview = null;
            linkInBio.logoUrl = null;
        }

// Updated handleLinkLogoUpload function
        function handleLinkLogoUpload(event, index) {
            const file = event.target.files[0];
            if (file) {
                linkInBio.links[index].logoFile = file;
                const reader = new FileReader();
                reader.onload = (e) => {
                    linkInBio.links[index].logoPreview = e.target.result;
                };
                reader.readAsDataURL(file);

                // Clear the existing logoUrl and reset remove flag
                linkInBio.links[index].logoUrl = null;
                linkInBio.links[index].removeLogo = false;
            }
        }

// Updated deleteLinkLogo function with no immediate DELETE request
        function deleteLinkLogo(index) {
            // Flag this link's logo for removal when saving
            linkInBio.links[index].removeLogo = true;

            // Clear logo data for UI updates
            linkInBio.links[index].logoFile = null;
            linkInBio.links[index].logoPreview = null;
            linkInBio.links[index].logoUrl = null;
        }

// Updated loadLinkDetails function to initialize removeLogo flags
        async function loadLinkDetails(linkId) {
            try {
                const response = await fetch(`/api/short-links/${linkId}`);
                if (!response.ok) {
                    throw new Error('Failed to fetch link details');
                }
                const linkDetails = await response.json();

                // Update form fields
                linkInBio.title = linkDetails.title || '';
                linkInBio.description = linkDetails.description || '';
                linkInBio.removeMainLogo = false;
                linkInBio.shortCode = linkDetails.shortCode || '';
                linkInBio.shortUrl = (baseUrl.value +'/'+linkDetails.shortCode) || '';
                linkInBio.originalUrl = linkDetails.originalUrl;
                linkInBio.linkType = linkDetails.linkType || '-1';
                // Update theme color
                if (linkDetails.themeColor) {
                    linkInBio.themeColor = linkDetails.themeColor;
                } else {
                    // Set default color based on theme type
                    linkInBio.themeColor = linkDetails.themeType === 'LIGHT' ? '#FFFFFF' : '#101223';
                }

                // Update layout type and theme type
                linkInBio.layoutType = linkDetails.layoutType.toLowerCase();
                linkInBio.themeType = linkDetails.themeType.toLowerCase();

                // Update main logo
                if (linkDetails.logoUrl) {
                    linkInBio.logoPreview = linkDetails.logoUrl;
                    linkInBio.logoUrl = linkDetails.logoUrl;
                    linkInBio.logoFile = null;
                } else {
                    linkInBio.logoPreview = null;
                    linkInBio.logoUrl = null;
                    linkInBio.logoFile = null;
                }

                // Update links
                if (linkDetails.links && linkDetails.links.length > 0) {
                    linkInBio.links = linkDetails.links.map(link => ({
                        title: link.title || '',
                        url: link.url || '',
                        logoFile: null,
                        logoPreview: link.logoUrl || null,
                        logoUrl: link.logoUrl || null,
                        removeLogo: false,
                        deleted: link.deleted || false,
                        isValidUrl: validateUrl(link)
                    }));
                } else {
                    // Reset to default if no links
                    linkInBio.links = [
                        {
                            title: 'Title',
                            url: '',
                            logoFile: null,
                            logoPreview: null,
                            logoUrl: null,
                            removeLogo: false,
                            deleted: false,
                            isValidUrl: false
                        }
                    ];
                }
            } catch (error) {
                console.error('Error loading link details:', error);
            }
        }

// Updated saveChanges function to handle logo flags
        async function saveChanges() {
            try {
                // Show loading state
                isSaving.value = true;

                // Collect form data
                const formData = new FormData();
                formData.append('title', linkInBio.title);
                formData.append('description', linkInBio.description);
                formData.append('themeColor', linkInBio.themeColor);
                formData.append('originalUrl', linkInBio.originalUrl);
                formData.append('linkType', linkInBio.linkType);
                formData.append('shortCode', linkInBio.shortCode);

                // Handle main logo
                if (linkInBio.logoFile) {
                    // New file uploaded
                    formData.append('logoFile', linkInBio.logoFile);
                } else if (linkInBio.logoUrl && !linkInBio.removeMainLogo) {
                    // Existing logo kept
                    formData.append('logoUrl', linkInBio.logoUrl);
                } else if (linkInBio.removeMainLogo) {
                    // Logo removed
                    formData.append('removeMainLogo', 'true');
                }

                // Add theme and layout
                formData.append('themeType', linkInBio.themeType.toUpperCase());
                formData.append('layoutType', linkInBio.layoutType.toUpperCase());

                if(linkInBio.linkType === 'BIO') {
                    // Collect links
                    for (let index = 0; index < linkInBio.links.length; index++) {
                        const link = linkInBio.links[index];

                        if (link.url && link.title) {
                            if(!validateUrl(link)) {
                                throw new Error('Failed to save link in bio');
                            }
                            formData.append(`links[${index}].title`, link.title);
                            formData.append(`links[${index}].url`, link.url);

                            if (link.logoFile) {
                                formData.append(`links[${index}].logoFile`, link.logoFile);
                            } else {
                                if (link.removeLogo) {
                                    formData.append(`links[${index}].removeLogo`, 'true');
                                } else if (link.logoUrl) {
                                    formData.append(`links[${index}].logoUrl`, link.logoUrl);
                                }
                            }
                            if (link.deleted) {
                                formData.append(`links[${index}].deleted`, 'true');
                            }
                        }else {
                            throw new Error('Failed to save link in bio');
                        }
                    }
                    linkInBio.links.forEach(link => {
                        link.removeLogo = false;
                        link.logoFile = null;
                    });
                }

                // Determine if this is an update or create
                const method = selectedLinkId.value ? 'PUT' : 'POST';
                const url = selectedLinkId.value ? `/api/short-links/${selectedLinkId.value}` : '/api/short-links';

                // Send to API
                const response = await fetch(url, {
                    method: method,
                    body: formData
                });

                if (!response.ok) {
                    const errorData = await response.json().catch(() => null);
                    throw new Error(errorData?.message || 'Failed to save link in bio');
                }

                // Reset remove flags after successful save
                linkInBio.removeMainLogo = false;
                linkInBio.links.forEach(link => {
                    link.removeLogo = false;
                });

                // Show success message
                alerts.success.show = true;
                alerts.error.show = false;
                setTimeout(() => {
                    alerts.success.show = false;
                }, 3000);

                // If this was a new creation, update the URL with the new ID
                if (!selectedLinkId.value) {
                    const responseData = await response.json();
                    if (responseData.id) {
                        selectedLinkId.value = responseData.id;
                        const newUrl = new URL(window.location.href);
                        newUrl.searchParams.set('id', responseData.id);
                        window.history.pushState({}, '', newUrl);
                    }
                }

                // Reload the links list in the sidebar
                await loadLinks();

            } catch (error) {
                // Show error message
                alerts.error.show = true;
                alerts.error.message = error.message;
                alerts.success.show = false;
            } finally {
                // Reset button state
                isSaving.value = false;
            }
        }

        // Delete functionality
        const showDeleteConfirmation = ref(false);
        const isDeleting = ref(false);

        async function deleteLink() {
            if (!selectedLinkId.value) return;

            try {
                isDeleting.value = true;

                // Call the delete endpoint
                const response = await fetch(`/api/short-links/${selectedLinkId.value}`, {
                    method: 'DELETE',
                });

                if (!response.ok) {
                    throw new Error('Failed to delete Link');
                }

                // Close the confirmation modal
                showDeleteConfirmation.value = false;

                // Show success message
                alerts.success.show = true;
                alerts.success.message = 'Link deleted successfully!';
                setTimeout(() => {
                    alerts.success.show = false;
                }, 3000);

                // Reset selected link and reload the sidebar links
                selectedLinkId.value = null;

                // Update URL parameters
                const newUrl = new URL(window.location.href);
                newUrl.searchParams.delete('id');
                window.history.pushState({}, '', newUrl);

                // Reload links in the sidebar
                await loadLinks();

                // Reset the active tab to the first tab
                activeTab.value = tabs[0].id;

            } catch (error) {
                console.error('Error deleting Link:', error);

                // Show error message
                alerts.error.show = true;
                alerts.error.message = error.message || 'Failed to delete Link';
                setTimeout(() => {
                    alerts.error.show = false;
                }, 5000);
            } finally {
                isDeleting.value = false;
            }
        }



        function copyToClipboard(text) {
            navigator.clipboard.writeText(text)
                .then(() => {
                    alerts.success.show = true;
                    alerts.success.message = 'URL copied to clipboard!';
                    setTimeout(() => {
                        alerts.success.show = false;
                    }, 3000);
                })
                .catch(err => {
                    console.error('Failed to copy URL: ', err);
                });
        }

        function openLink(url) {
            window.open(`${url}`, '_blank');
        }

        // Lifecycle hooks
        onMounted(() => {
            console.log('on mounted');
            loadLinks();
        });

        function undoDelete(index) {
            linkInBio.links[index].deleted = false;
        }

        // URL validation function
        function validateUrl(link) {
            console.log('link=' + link.url);

            if (!link.url) {
                link.isValidUrl = false;
                return;
            }

            let testUrl = link.url.trim();

            // Add https:// if missing
            if (!/^https?:\/\//i.test(testUrl)) {
                testUrl = 'https://' + testUrl;
            }
            link.url = testUrl;

            // Regex to validate public URLs with a domain (like .com, .net, etc.)
            const urlRegex = /^https?:\/\/[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)+(:\d+)?(\/[^\s]*)?$/;

            if (urlRegex.test(testUrl)) {
                link.isValidUrl = true;
            } else {
                link.isValidUrl = false;
            }

            return link.isValidUrl;
        }


        return {
            loggedInUser,
            links,
            selectedLinkId,
            tabs,
            activeTab,
            appShortener,
            urlShortener,
            linkInBio,
            qrTag,
            layouts,
            themes,
            alerts,
            isSaving,
            showDeleteConfirmation,
            isDeleting,

            // Methods
            validateUrl,
            baseUrl,
            copyToClipboard,
            openLink,
            deleteLink,
            selectLink,
            handleLogoUpload,
            deleteLogo,
            handleLinkLogoUpload,
            deleteLinkLogo,
            addLink,
            softDeleteLink,
            createNewLink,
            saveChanges,
            handleQRTagImageUpload,
            undoDelete
        };
    }
}).mount('#app');