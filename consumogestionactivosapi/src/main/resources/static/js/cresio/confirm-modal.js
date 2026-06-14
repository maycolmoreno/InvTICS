(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {
    var modalElement = document.getElementById("cuiConfirmModal");
    var messageElement = document.getElementById("cuiConfirmMessage");
    var acceptButton = document.getElementById("cuiConfirmAccept");
    var pendingAction = null;

    function fallbackConfirm(trigger, event) {
      var message = trigger.getAttribute("data-cui-confirm") || "Confirma esta accion.";
      if (!window.confirm(message)) {
        event.preventDefault();
        event.stopPropagation();
      }
    }

    function submitForm(trigger) {
      var form = trigger.closest("form");
      if (form) {
        if (trigger.name && !form.querySelector("input[type='hidden'][name='" + trigger.name + "']")) {
          var hidden = document.createElement("input");
          hidden.type = "hidden";
          hidden.name = trigger.name;
          hidden.value = trigger.value;
          form.appendChild(hidden);
        }
        form.submit();
      }
    }

    if (acceptButton) {
      acceptButton.addEventListener("click", function () {
        if (!pendingAction) {
          return;
        }

        var action = pendingAction;
        pendingAction = null;

        if (action.type === "link") {
          window.location.href = action.href;
          return;
        }

        if (action.type === "form") {
          submitForm(action.trigger);
        }
      });
    }

    document.querySelectorAll("[data-cui-confirm]").forEach(function (trigger) {
      trigger.addEventListener("click", function (event) {
        var message = trigger.getAttribute("data-cui-confirm") || "Confirma esta accion.";

        if (!modalElement || !messageElement || !acceptButton || !window.bootstrap) {
          fallbackConfirm(trigger, event);
          return;
        }

        event.preventDefault();
        event.stopPropagation();
        messageElement.textContent = message;

        if (trigger.tagName === "A" && trigger.href) {
          pendingAction = { type: "link", href: trigger.href };
        } else {
          pendingAction = { type: "form", trigger: trigger };
        }

        window.bootstrap.Modal.getOrCreateInstance(modalElement).show();
      });
    });
  });
})();
