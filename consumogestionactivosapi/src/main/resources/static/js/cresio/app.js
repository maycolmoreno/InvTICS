(function () {
  "use strict";

  function markCurrentNavigation() {
    var path = window.location.pathname;
    document.querySelectorAll(".cui-sidebar a[href]").forEach(function (link) {
      var href = link.getAttribute("href");
      if (!href || href === "javascript:void(0);") {
        return;
      }
      if (path === href || (href !== "/" && path.indexOf(href) === 0)) {
        link.classList.add("is-current");
        var group = link.closest(".cui-nav-group");
        if (group) {
          group.classList.add("active", "is-open");
          var toggle = group.querySelector(".cui-nav-toggle");
          if (toggle) {
            toggle.setAttribute("aria-expanded", "true");
          }
        }
      }
    });
  }

  function moveModalsToBody() {
    document.querySelectorAll(".modal").forEach(function (modal) {
      if (modal.parentElement !== document.body) {
        document.body.appendChild(modal);
      }
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    markCurrentNavigation();
    moveModalsToBody();
  });
})();
