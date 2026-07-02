(function () {
  "use strict";

  function marcarInvalido(campo, mensaje) {
    campo.classList.add("is-invalid");
    var feedback = campo.parentElement.querySelector(".invalid-feedback[data-baja-feedback]");
    if (!feedback) {
      feedback = document.createElement("div");
      feedback.className = "invalid-feedback";
      feedback.setAttribute("data-baja-feedback", "");
      campo.parentElement.appendChild(feedback);
    }
    feedback.textContent = mensaje;
  }

  function limpiarInvalido(campo) {
    campo.classList.remove("is-invalid");
  }

  function validarCampos(form) {
    var valido = true;
    var primerInvalido = null;
    form.querySelectorAll("[data-baja-required]").forEach(function (campo) {
      var valor = (campo.value || "").trim();
      if (!valor) {
        marcarInvalido(campo, "Este campo es obligatorio.");
        valido = false;
        primerInvalido = primerInvalido || campo;
      } else {
        limpiarInvalido(campo);
      }
    });
    if (primerInvalido) {
      primerInvalido.focus();
    }
    return valido;
  }

  function actualizarMensajeConfirmacion(form) {
    var trigger = form.querySelector("[data-cui-confirm]");
    if (!trigger) return;
    var labelEl = form.querySelector("[data-activo-label]");
    var nombreActivo = labelEl
      ? (labelEl.tagName === "SELECT"
          ? (labelEl.options[labelEl.selectedIndex] ? labelEl.options[labelEl.selectedIndex].text.trim() : "este activo")
          : (labelEl.textContent || labelEl.value || "este activo").trim())
      : "este activo";
    trigger.setAttribute(
      "data-cui-confirm",
      "¿Confirma dar de baja a: " + nombreActivo + "?\n\nEsta acción es permanente y no puede revertirse."
    );
  }

  function inicializarConfirmacionFuerte(form) {
    var input = form.querySelector("[data-baja-confirm-input]");
    var trigger = form.querySelector("[data-cui-confirm]");
    if (!input || !trigger) return;
    trigger.disabled = true;
    input.addEventListener("input", function () {
      trigger.disabled = input.value.trim().toUpperCase() !== "BAJA";
    });
  }

  // Prellena el formulario cuando se llega desde el boton "Iniciar baja" de
  // una OT (resultadoTecnico IRREPARABLE/REQUIERE_BAJA). Solo carga datos:
  // no marca el campo de confirmacion ni envia el formulario.
  function precargarDesdeOT(form) {
    var params = new URLSearchParams(window.location.search);
    var otId = params.get("nuevoDesdeOT");
    if (!otId) return;

    var equipoId = params.get("equipoId");
    var motivo = params.get("motivo");
    var observacion = params.get("observacion");

    var selectActivo = form.querySelector("#select-activo");
    if (selectActivo && equipoId) {
      var existe = Array.prototype.some.call(selectActivo.options, function (opt) {
        return opt.value === equipoId;
      });
      if (existe) {
        selectActivo.value = equipoId;
        selectActivo.dispatchEvent(new Event("change"));
      }
    }

    var selectMotivo = form.querySelector('[name="motivo"]');
    if (selectMotivo && motivo) {
      selectMotivo.value = motivo;
      selectMotivo.dispatchEvent(new Event("change"));
    }

    var textareaObs = form.querySelector('[name="observacion"]');
    if (textareaObs && observacion) {
      textareaObs.value = observacion;
      textareaObs.dispatchEvent(new Event("input"));
    }

    var banner = document.getElementById("banner-desde-ot");
    var bannerTexto = document.getElementById("banner-desde-ot-texto");
    if (banner && bannerTexto) {
      bannerTexto.textContent = "OT-" + String(otId).padStart(5, "0");
      banner.hidden = false;
    }
  }

  document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("form[data-baja-activo]").forEach(function (form) {
      var trigger = form.querySelector("[data-cui-confirm]");

      precargarDesdeOT(form);

      actualizarMensajeConfirmacion(form);

      var labelEl = form.querySelector("[data-activo-label]");
      if (labelEl) {
        labelEl.addEventListener("change", function () { actualizarMensajeConfirmacion(form); });
      }

      inicializarConfirmacionFuerte(form);

      // Captura el click ANTES que confirm-modal.js: si los campos son
      // invalidos, bloquea la apertura del modal de confirmacion.
      if (trigger) {
        trigger.addEventListener("click", function (e) {
          if (!validarCampos(form)) {
            e.preventDefault();
            e.stopImmediatePropagation();
          }
        }, true);
      }

      form.querySelectorAll("[data-baja-required]").forEach(function (campo) {
        campo.addEventListener("input", function () { limpiarInvalido(campo); });
        campo.addEventListener("change", function () { limpiarInvalido(campo); });
      });
    });
  });
})();
