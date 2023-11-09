<h2>Inicio de sesi&oacute;n</h2>
<div class="formulario">
    <form id="formulario-registro" action="index?opcion=login" method="post">
        <div class="form_group">
            <input class="form_field" type="text" id="dni" name="dni" placeholder="Ej: 12345678A" required>
            <label class="form_label">DNI:</label>
        </div>
        <div class="form_group">
            <input class="form_field" type="text" id="email" name="email" placeholder="Ej: email@hotmail.com" required>
            <label class="form_label">Email:</label>
        </div>
        <div class="form_group">
            <input class="form_field" type="password" id="contrasenya" name="contrasenya" required>
            <label class="form_label">Contrase&ntilde;a:</label>
        </div>
        <input type="submit" value="Acceder">
    </form>
</div>
