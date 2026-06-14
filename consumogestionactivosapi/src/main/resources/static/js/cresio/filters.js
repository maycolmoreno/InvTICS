(function () {
  "use strict";

  var storageKeyPrefix = "cresio.grid.filter.";

  function keyFor(control) {
    var grid = control.closest("[data-cui-grid]");
    var gridId = grid ? grid.id : "global";
    var name = control.getAttribute("data-filter-key") || control.getAttribute("data-cui-filter-name") || control.name || control.id;
    return storageKeyPrefix + gridId + "." + name;
  }

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-cui-filter-persist]").forEach(function (control) {
      var key = keyFor(control);
      var saved = window.localStorage ? window.localStorage.getItem(key) : null;
      if (saved !== null) {
        control.value = saved;
        control.dispatchEvent(new Event("change", { bubbles: true }));
      }
      control.addEventListener("change", function () {
        if (window.localStorage) {
          window.localStorage.setItem(key, control.value);
        }
      });
    });
  });
})();
