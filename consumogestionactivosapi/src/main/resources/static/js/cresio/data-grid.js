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

  function updateCount(grid, visible, total) {
    var target = grid.querySelector("[data-cui-grid-count]");
    if (target) {
      target.textContent = visible + " de " + total + " registros";
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

    updateCount(grid, visible, rows.length);
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
        controls.forEach(function (control) {
          control.value = "";
        });
        applyGrid(grid);
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
