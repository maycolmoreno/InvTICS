(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-cui-confirm]").forEach(function (trigger) {
      trigger.addEventListener("click", function (event) {
        var message = trigger.getAttribute("data-cui-confirm") || "Confirma esta accion.";
        if (!window.confirm(message)) {
          event.preventDefault();
          event.stopPropagation();
        }
      });
    });
  });
})();
