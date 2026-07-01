(function () {
  "use strict";

  function serializeDrawerForms(drawer) {
    var data = [];
    drawer.querySelectorAll("form").forEach(function (form) {
      data.push(new URLSearchParams(new FormData(form)).toString());
    });
    return data.join("&");
  }

  function isDrawerDirty(drawer) {
    if (typeof drawer.dataset.cuiDrawerSnapshot !== "string") {
      return false;
    }
    return serializeDrawerForms(drawer) !== drawer.dataset.cuiDrawerSnapshot;
  }

  function openDrawer(target) {
    var drawer = document.querySelector(target || "#cuiDrawer");
    var backdrop = document.querySelector("[data-cui-drawer-backdrop]");
    if (!drawer) {
      return;
    }
    drawer.classList.add("is-open");
    drawer.dataset.cuiDrawerSnapshot = serializeDrawerForms(drawer);
    if (backdrop) {
      backdrop.classList.add("is-open");
    }
    drawer.setAttribute("aria-hidden", "false");
  }

  function closeDrawer() {
    var openDrawers = document.querySelectorAll(".cui-drawer.is-open");
    var hasUnsavedChanges = Array.prototype.some.call(openDrawers, isDrawerDirty);
    if (hasUnsavedChanges && !window.confirm("Hay cambios sin guardar en el formulario. ¿Deseas cerrar de todas formas?")) {
      return;
    }
    openDrawers.forEach(function (drawer) {
      drawer.classList.remove("is-open");
      drawer.setAttribute("aria-hidden", "true");
      delete drawer.dataset.cuiDrawerSnapshot;
    });
    document.querySelectorAll("[data-cui-drawer-backdrop]").forEach(function (backdrop) {
      backdrop.classList.remove("is-open");
    });
  }

  document.addEventListener("click", function (event) {
    var openButton = event.target.closest("[data-cui-drawer-open]");
    if (openButton) {
      event.preventDefault();
      openDrawer(openButton.getAttribute("data-cui-drawer-open"));
      return;
    }

    var closeTrigger = event.target.closest("[data-cui-drawer-close], [data-cui-drawer-backdrop]");
    if (closeTrigger) {
      event.preventDefault();
      closeDrawer();
    }
  });

  document.addEventListener("DOMContentLoaded", function () {
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
