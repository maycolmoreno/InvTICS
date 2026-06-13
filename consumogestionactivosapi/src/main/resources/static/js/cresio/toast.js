(function () {
  "use strict";

  function showToast(message, variant) {
    var container = document.querySelector("[data-cui-toast-container]");
    if (!container || !message) {
      return;
    }

    var toast = document.createElement("div");
    toast.className = "toast align-items-center text-bg-" + (variant || "primary") + " border-0 show mb-2";
    toast.setAttribute("role", "status");
    toast.setAttribute("aria-live", "polite");
    toast.innerHTML = '<div class="d-flex"><div class="toast-body"></div><button type="button" class="btn-close btn-close-white me-2 m-auto" aria-label="Cerrar"></button></div>';
    toast.querySelector(".toast-body").textContent = message;
    toast.querySelector("button").addEventListener("click", function () {
      toast.remove();
    });
    container.appendChild(toast);
    setTimeout(function () {
      toast.remove();
    }, 5000);
  }

  window.CresioToast = {
    show: showToast
  };
})();
