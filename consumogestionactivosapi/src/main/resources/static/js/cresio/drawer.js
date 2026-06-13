(function () {
  "use strict";

  function openDrawer(target) {
    var drawer = document.querySelector(target || "#cuiDrawer");
    var backdrop = document.querySelector("[data-cui-drawer-backdrop]");
    if (!drawer) {
      return;
    }
    drawer.classList.add("is-open");
    if (backdrop) {
      backdrop.classList.add("is-open");
    }
    drawer.setAttribute("aria-hidden", "false");
  }

  function closeDrawer() {
    document.querySelectorAll(".cui-drawer.is-open").forEach(function (drawer) {
      drawer.classList.remove("is-open");
      drawer.setAttribute("aria-hidden", "true");
    });
    document.querySelectorAll("[data-cui-drawer-backdrop]").forEach(function (backdrop) {
      backdrop.classList.remove("is-open");
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-cui-drawer-open]").forEach(function (button) {
      button.addEventListener("click", function () {
        openDrawer(button.getAttribute("data-cui-drawer-open"));
      });
    });

    document.querySelectorAll("[data-cui-drawer-close], [data-cui-drawer-backdrop]").forEach(function (button) {
      button.addEventListener("click", closeDrawer);
    });

    document.addEventListener("keydown", function (event) {
      if (event.key === "Escape") {
        closeDrawer();
      }
    });
  });

  window.CresioDrawer = {
    open: openDrawer,
    close: closeDrawer
  };
})();
