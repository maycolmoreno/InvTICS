(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-cui-tab-hash]").forEach(function (trigger) {
      trigger.addEventListener("shown.bs.tab", function (event) {
        var target = event.target.getAttribute("data-bs-target");
        if (target && window.history && window.history.replaceState) {
          window.history.replaceState(null, "", target);
        }
      });
    });

    if (window.location.hash) {
      var tab = document.querySelector("[data-bs-target='" + window.location.hash + "']");
      if (tab && window.bootstrap) {
        window.bootstrap.Tab.getOrCreateInstance(tab).show();
      }
    }
  });
})();
