(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".cui-action-menu .dropdown-item").forEach(function (item) {
      item.addEventListener("click", function () {
        var menu = item.closest(".dropdown-menu");
        if (!menu) {
          return;
        }
        var toggle = menu.previousElementSibling;
        if (window.bootstrap && toggle) {
          var dropdown = window.bootstrap.Dropdown.getInstance(toggle);
          if (dropdown) {
            dropdown.hide();
          }
        }
      });
    });
  });
})();
