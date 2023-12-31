document.addEventListener('DOMContentLoaded', function () {
    const formularioRegistro = document.querySelector('#formulario-registro');
    if (formularioRegistro) {
        formularioRegistro.addEventListener('submit', validarFormulario)
    }

    const formularioConsulta = document.querySelector('#formulario-consulta-dni');
    if (formularioConsulta) {
        formularioConsulta.addEventListener('submit', validarFormulario)
    }
    const formularioBusqueda = document.querySelector('#formulario-filtro-busqueda-empleado');
    if (formularioBusqueda) {
        formularioBusqueda.addEventListener('submit', validarFormularioBusqueda)
    }

})


function validarFormulario(event) {
    const sexoValue = event.target.querySelector('input[name=sexo]').value
    let dniValue = document.querySelector('input[name=dni]').value;
    return validarDNI(event, dniValue) || validarSexo(event, sexoValue)
}

function validarFormularioBusqueda(event) {
    const campo = event.target.querySelector('select[name=campo]').value
    const valor = event.target.querySelector('input[name=valor]').value
    switch (campo) {
        case "dni":
            validarDNI(event, valor);
            break;
        case "sexo":
            validarSexo(event, valor);
            break;
        case "categoria":
            validarCategoria(event, valor);
            break;
        case "salario":
            validarSalario(event, valor);
            break;
        case "anyos":
            validarAntiguedad(event, valor);
            break;
    }
}

function validarDNI(event, dni) {
    let regex = /^\d{8}[A-Z]$/;
    if (!regex.test(dni)) {
        alert("Por favor, introduce un DNI válido (8 dígitos seguido de una letra).");
        event.preventDefault();
        return false;
    }

    return true;
}

function validarSexo(event, value) {
    if (!['f', 'm', 'i'].includes(value)) {
        event.preventDefault();
        alert('El sexo tiene un valor incorrecto, debe ser "f", "m" o "i"')
        return false
    }
    return true;
}

function validarCategoria(event, value) {
    let valido;
    try {
        const valueParsed = parseInt(value);
        valido = valueParsed > 0 && valueParsed < 10;
    } catch (e) {
        valido = false;
    }

    if (!valido) {
        event.preventDefault();
        alert('La categoría tiene un valor incorrecto, debe ser un número entero del 1 al 9')
    }
    return valido;

}

function validarSalario(event, value) {
    let valido;
    try {
        const valueParsed = parseFloat(value);
        valido = valueParsed >= 0;
    } catch (e) {
        valido = false;
    }

    if (!valido) {
        event.preventDefault();
        alert('El salario tiene un valor incorrecto, debe ser un número numérico y mayor a 0')
    }
    return valido;

}

function validarAntiguedad(event, value) {
    let valido;
    try {
        const valueParsed = parseFloat(value);
        valido = valueParsed >= 0;
    } catch (e) {
        valido = false;
    }

    if (!valido) {
        event.preventDefault();
        alert('La antigüedad tiene un valor incorrecto, debe ser un número numérico y mayor a 0')
    }
    return valido;
}

function resetearFormulario(event) {
    const opcion = event.target.parentElement.parentElement.querySelector('input[name=opcion]').value;
    const params = window.location.search;
    const url = window.location.href.replace(params, '?opcion=' + opcion);
    window.location.replace(url);
    event.preventDefault();
}