document.addEventListener("DOMContentLoaded", function () {
    const buttons = document.querySelectorAll('[type="tab-button"]');
    const tabs = document.querySelectorAll('[type="tab"]');

    function activateTab(id) {
        tabs.forEach(tab => {
            tab.style.display = tab.id === id ? 'block' : 'none';
        });

        buttons.forEach(btn => {
            if (btn.getAttribute('for') === id) {
                btn.classList.add('btn-dark');
                btn.classList.remove('btn-outline-secondary');
            } else {
                btn.classList.remove('btn-dark');
                btn.classList.add('btn-outline-secondary');
            }
        });
    }

    buttons.forEach(button => {
        button.addEventListener('click', () => {
            const targetId = button.getAttribute('for');
            activateTab(targetId);
        });
    });

    // Initialize first tab
    if (tabs.length > 0) {
        activateTab(tabs[0].id);
    }
});