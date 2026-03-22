/**
 * CRESIO - Sidebar accordion behavior
 * Usa MutationObserver para detectar cuando NEXEL abre un menu
 * y cierra automaticamente los demas. No interfiere con el click de NEXEL.
 */
(function () {
  "use strict";

  var processing = false;

  function collapseOthers(openedItem) {
    if (processing) return;
    processing = true;

    var siblings = Array.from(
      openedItem.parentElement.querySelectorAll(":scope > .nxl-hasmenu")
    );

    siblings.forEach(function (s) {
      if (s === openedItem) return;
      if (!s.classList.contains("nxl-trigger")) return;

      s.classList.remove("nxl-trigger");
      var sub = s.querySelector(":scope > .nxl-submenu");
      if (sub) {
        sub.style.display = "none";
        sub.style.height = "";
      }
    });

    setTimeout(function () {
      processing = false;
    }, 100);
  }

  function collapseAllExceptActive() {
    var nav = document.querySelector(".nxl-navigation");
    if (!nav) return;

    nav.querySelectorAll(".nxl-hasmenu").forEach(function (item) {
      var hasActive = item.querySelector(".nxl-item.active > .nxl-link, .nxl-link.active");
      if (hasActive) return;

      item.classList.remove("nxl-trigger");
      var sub = item.querySelector(":scope > .nxl-submenu");
      if (sub) {
        sub.style.display = "none";
        sub.style.height = "";
      }
    });
  }

  function init() {
    var nav = document.querySelector(".nxl-navigation");
    if (!nav) return;

    collapseAllExceptActive();

    var observer = new MutationObserver(function (mutations) {
      mutations.forEach(function (mutation) {
        if (mutation.type !== "attributes" || mutation.attributeName !== "class") return;

        var item = mutation.target;
        if (!item.classList.contains("nxl-hasmenu")) return;
        if (!item.classList.contains("nxl-trigger")) return;

        collapseOthers(item);
      });
    });

    nav.querySelectorAll(".nxl-hasmenu").forEach(function (item) {
      observer.observe(item, { attributes: true, attributeFilter: ["class"] });
    });
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      setTimeout(init, 100);
    });
  } else {
    setTimeout(init, 100);
  }
})();
