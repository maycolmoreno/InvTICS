(function () {
  "use strict";

  function toggleSidebar() {
    if (window.matchMedia("(max-width: 991.98px)").matches) {
      document.body.classList.toggle("cui-sidebar-mobile-open");
      return;
    }
    document.body.classList.toggle("cui-sidebar-collapsed");
    localStorage.setItem(
      "cui-sidebar-collapsed",
      document.body.classList.contains("cui-sidebar-collapsed") ? "1" : "0"
    );
  }

  function restoreSidebarState() {
    if (localStorage.getItem("cui-sidebar-collapsed") === "1") {
      document.body.classList.add("cui-sidebar-collapsed");
    }
  }

  function markActiveLink() {
    var path = window.location.pathname;
    if (path.endsWith("/") && path.length > 1) {
      path = path.slice(0, -1);
    }

    var bestMatch = null;
    var bestMatchLength = 0;

    document.querySelectorAll(".cui-nav-link").forEach(function (link) {
      var href = link.getAttribute("href");
      if (!href || href === "/" || href === "#") return;
      if (href.endsWith("/") && href.length > 1) {
        href = href.slice(0, -1);
      }
      if (path === href || path.startsWith(href + "/")) {
        if (href.length > bestMatchLength) {
          bestMatchLength = href.length;
          bestMatch = link;
        }
      }
    });

    if (bestMatch) {
      bestMatch.classList.add("is-current");
      var group = bestMatch.closest(".cui-nav-group");
      if (group) {
        group.classList.add("is-open");
        var toggle = group.querySelector(".cui-nav-toggle");
        if (toggle) toggle.setAttribute("aria-expanded", "true");
      }
    }
  }

  function initSubmenus() {
    document.querySelectorAll(".cui-nav-toggle").forEach(function (button) {
      button.addEventListener("click", function () {
        var group = button.closest(".cui-nav-group");
        if (!group) return;
        group.classList.toggle("is-open");
        button.setAttribute(
          "aria-expanded",
          group.classList.contains("is-open") ? "true" : "false"
        );
      });
    });
  }

  function initNavSearch() {
    var input = document.getElementById("cuiNavSearch");
    if (!input) return;

    var scroll = document.querySelector(".cui-sidebar-scroll");
    var emptyMsg = null;

    input.addEventListener("input", function () {
      var query = this.value.toLowerCase().trim();

      // Eliminar mensaje de vacío anterior
      if (emptyMsg) {
        emptyMsg.remove();
        emptyMsg = null;
      }

      if (!query) {
        // Restaurar todo
        document.querySelectorAll(".cui-nav-section").forEach(function (s) {
          s.style.display = "";
        });
        document.querySelectorAll(".cui-nav-list > li").forEach(function (li) {
          li.style.display = "";
        });
        return;
      }

      var totalVisible = 0;

      document.querySelectorAll(".cui-nav-section").forEach(function (section) {
        var sectionVisible = 0;

        section.querySelectorAll(".cui-nav-list > li").forEach(function (li) {
          // Texto del ítem raíz
          var navText = li.querySelector(".cui-nav-text");
          var rootText = navText ? navText.textContent.toLowerCase() : "";

          // Texto de los ítems del submenú
          var subTexts = [];
          li.querySelectorAll(".cui-nav-submenu .cui-nav-link").forEach(function (sub) {
            subTexts.push(sub.textContent.toLowerCase());
          });

          var rootMatch = rootText.includes(query);
          var subMatch = subTexts.some(function (t) { return t.includes(query); });

          if (rootMatch || subMatch) {
            li.style.display = "";
            sectionVisible++;
            totalVisible++;
            // Expandir grupo si coincidió por subítem
            if (subMatch && !rootMatch) {
              var group = li;
              if (group.classList.contains("cui-nav-group")) {
                group.classList.add("is-open");
                var toggle = group.querySelector(".cui-nav-toggle");
                if (toggle) toggle.setAttribute("aria-expanded", "true");
              }
            }
          } else {
            li.style.display = "none";
          }
        });

        section.style.display = sectionVisible > 0 ? "" : "none";
      });

      // Mostrar mensaje si no hay resultados
      if (totalVisible === 0 && scroll) {
        emptyMsg = document.createElement("p");
        emptyMsg.className = "cui-nav-search-empty";
        emptyMsg.textContent = 'Sin resultados para "' + this.value + '"';
        scroll.appendChild(emptyMsg);
      }
    });

    // Limpiar con Escape
    input.addEventListener("keydown", function (e) {
      if (e.key === "Escape") {
        this.value = "";
        this.dispatchEvent(new Event("input"));
        this.blur();
      }
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    restoreSidebarState();
    markActiveLink();
    initSubmenus();
    initNavSearch();

    document.querySelectorAll("[data-cui-sidebar-toggle]").forEach(function (button) {
      button.addEventListener("click", toggleSidebar);
    });
  });
})();
