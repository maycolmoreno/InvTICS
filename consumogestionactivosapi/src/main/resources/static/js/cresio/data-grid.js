(function () {
  "use strict";

  function normalize(value) {
    return (value || "").toString().trim().toLowerCase()
      .normalize("NFD").replace(/[̀-ͯ]/g, "");
  }

  function matchesSearch(row, term) {
    if (!term) {
      return true;
    }
    return normalize(row.getAttribute("data-search") || row.textContent).indexOf(term) >= 0;
  }

  function matchesFilters(row, grid) {
    var filters = grid.querySelectorAll("[data-cui-grid-filter]");
    for (var i = 0; i < filters.length; i += 1) {
      var filter = filters[i];
      var value = normalize(filter.value);
      var key = filter.getAttribute("data-filter-key");
      if (!value || !key) {
        continue;
      }
      if (normalize(row.getAttribute("data-" + key)) !== value) {
        return false;
      }
    }
    return true;
  }

  // Etiquetas legibles de los filtros activos (busqueda + selects), para que
  // un filtro guardado entre sesiones (data-cui-filter-persist) nunca oculte
  // datos nuevos sin que quede a la vista por que.
  function activeFilterLabels(grid) {
    var labels = [];
    var search = grid.querySelector("[data-cui-grid-search]");
    if (search && search.value.trim() !== "") {
      labels.push('"' + search.value.trim() + '"');
    }
    grid.querySelectorAll("[data-cui-grid-filter]").forEach(function (filter) {
      if (!filter.value) {
        return;
      }
      var opt = filter.options && filter.selectedIndex >= 0 ? filter.options[filter.selectedIndex] : null;
      labels.push(opt ? opt.textContent.trim() : filter.value);
    });
    return labels;
  }

  function toolbarActions(grid) {
    return grid.querySelector(".cui-toolbar-actions");
  }

  function ensureClearFiltersButton(grid) {
    var container = toolbarActions(grid);
    if (!container) {
      return null;
    }
    var btn = container.querySelector("[data-cui-grid-clear-filters]");
    if (!btn) {
      btn = document.createElement("button");
      btn.type = "button";
      btn.className = "btn btn-light cui-btn-soft";
      btn.setAttribute("data-cui-grid-clear-filters", "");
      btn.innerHTML = '<i class="feather-x-circle" aria-hidden="true"></i><span>Limpiar filtros</span>';
      btn.addEventListener("click", function () {
        resetGridControls(grid);
      });
      container.insertBefore(btn, container.firstChild);
    }
    return btn;
  }

  function resetGridControls(grid) {
    grid.querySelectorAll("[data-cui-grid-search], [data-cui-grid-filter]").forEach(function (control) {
      control.value = "";
      control.dispatchEvent(new Event("change", { bubbles: true }));
    });
    applyGrid(grid);
  }

  function updateCount(grid, visible, total, labels) {
    var target = grid.querySelector("[data-cui-grid-count]");
    if (target) {
      var texto = visible + " de " + total + " registros";
      if (labels.length > 0) {
        texto += " · Filtrado por " + labels.join(", ");
      }
      target.textContent = texto;
    }
    var clearBtn = ensureClearFiltersButton(grid);
    if (clearBtn) {
      clearBtn.hidden = labels.length === 0;
    }
  }

  function updateEmptyState(grid, visible) {
    var empty = grid.querySelector("[data-cui-empty-state]");
    if (empty) {
      empty.hidden = visible > 0;
    }
  }

  function applyGrid(grid) {
    var search = grid.querySelector("[data-cui-grid-search]");
    var term = normalize(search ? search.value : "");
    var rows = Array.prototype.slice.call(grid.querySelectorAll("[data-cui-grid-row]"));
    var visible = 0;

    rows.forEach(function (row) {
      var show = matchesSearch(row, term) && matchesFilters(row, grid);
      row.hidden = !show;
      if (show) {
        visible += 1;
      }
    });

    updateCount(grid, visible, rows.length, activeFilterLabels(grid));
    updateEmptyState(grid, visible);
  }

  function bindGrid(grid) {
    var controls = grid.querySelectorAll("[data-cui-grid-search], [data-cui-grid-filter]");
    controls.forEach(function (control) {
      var eventName = control.matches("input[type='search'], input[type='text']") ? "input" : "change";
      control.addEventListener(eventName, function () {
        applyGrid(grid);
      });
    });

    grid.querySelectorAll("[data-cui-filter-reset]").forEach(function (reset) {
      reset.addEventListener("click", function () {
        resetGridControls(grid);
      });
    });

    grid.querySelectorAll("[data-cui-column-toggle]").forEach(function (toggle) {
      toggle.addEventListener("change", function () {
        var key = toggle.value;
        grid.querySelectorAll("[data-cui-column='" + key + "']").forEach(function (cell) {
          cell.hidden = !toggle.checked;
        });
      });
    });

    applyGrid(grid);
  }

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-cui-grid]").forEach(bindGrid);
  });
})();
