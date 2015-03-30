<?php include('header.php'); ?>

<input type="hidden" name="type" value="<?php echo $type->id ?>">

<div class="wrapper full"><div class="clearfix">
	<section class="portlet filters clearfix">
		<header>
			<h1>Filtrar juegos</h1>
		</header>
		<dl class="column">
			<dt>Título</dt>
			<dd><input type="search" value="" id="s" class="text" placeholder="Título"></dd>
			<dt>Código</dt>
			<dd><input type="text" value="" id="code" class="text" placeholder="SLPS-XXXX, SLES-XXXX, CUSA-XXXX, UPC, EAN,..."></dd>
			<dt>Año</dt>
			<dd>				
				<div class="clearfix">
					<div class="year left"><input id="yearFrom" type="text"></div>
					<div id="yearsSlider"><div></div></div>
					<div class="year right alignRight"><input id="yearTo" type="text"></div>
				</div>
			</dd>
			<dt>Plataforma</dt>
			<dd>
				<select id="platform" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando plataformas...</option>
				</select>
			</dd>
			<dt>Región</dt>
			<dd>
				<select id="region" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando regiones...</option>
				</select>
			</dd>
		</dl>
		<dl class="column">
			<dt>Género</dt>
			<dd>
				<select id="genre" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando géneros...</option>
				</select>
			</dd>
			<dt>Desarrollado por</dt>
			<dd>
				<select id="developer" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando compañías...</option>
				</select>
			</dd>
			<dt>Publicado por</dt>
			<dd>
				<select id="publisher" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando compañías...</option>
				</select>
			</dd>
			<dt>Formato</dt>
			<dd>
				<select id="format" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando formatos...</option>
				</select>
			</dd>
			<dt>Etiqueta</dt>
			<dd>
				<select id="tag" class="combo">
					<option value="">Cualquiera</option>
					<option value="" class="loading">Cargando etiquetas...</option>
				</select>
			</dd>
		</dl>
	</section>
	<div class="content"></div>
	<div id="results"></div>
</div></div>

<?php include('footer.php'); ?>