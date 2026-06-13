(function () {
  "use strict";

  function toggleSidebar() {
    if (window.matchMedia("(max-width: 991.98px)").matches) {
      document.body.classList.toggle("cui-sidebar-mobile-open");
      return;
    }
    document.body.classList.toggle("cui-sidebar-collapsed");
    localStorage.setItem("cui-sidebar-collapsed", document.body.classList.contains("cui-sidebar-collapsed") ? "1" : "0");
  }

  function restoreSidebarState() {
    if (localStorage.getItem("cui-sidebar-collapsed") === "1") {
      document.body.classList.add("cui-sidebar-collapsed");
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    restoreSidebarState();

    document.querySelectorAll("[data-cui-sidebar-toggle]").forEach(function (button) {
      button.addEventListener("click", toggleSidebar);
    });

    document.querySelectorAll(".cui-nav-toggle").forEach(function (button) {
      button.addEventListener("click", function () {
        var group = button.closest(".cui-nav-group");
        if (!group) {
          return;
        }
        group.classList.toggle("is-open");
        button.setAttribute("aria-expanded", group.classList.contains("is-open") ? "true" : "false");
      });
    });
  });
})();
