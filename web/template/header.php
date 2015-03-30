<!DOCTYPE html>
<html lang="es">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title>Nueva Partida</title>
	<meta name="description" content="La mayor base de datos sobre juegos de consola en CD">
	<meta name="keywords" content="juegos, cd, playstation, saturn, mega cd, amiga cd32, pc-fx, jaguar, pc engine, turbo duo, 3do, cdtv, fm towns marty, neo geo cd, pippin, playdia">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
	<meta name="robots" content="follow, all, noodp">

	<link rel="stylesheet" href="<?php echo BASE_URL; ?>styles/styles.css">
	<link rel="stylesheet" href="//fonts.googleapis.com/css?family=Lato:400italic,700italic,400,700">
	<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/themes/smoothness/jquery-ui.css">
</head>
<body>
	<div id="full">
	<header class="head" id="nav">
		<div class="wrapper clearfix">
			<h1>
				<a href="<?php echo BASE_URL; ?>">Nueva Partida</a>
			</h1>
			<nav>
				<a href="#nav" title="Mostrar Categorías" class="menu mostrar">Menú</a>
				<a href="#" title="Ocultar Categorías" class="menu ocultar">Cerrar</a>
				<ul>
					<li><a href="<?php echo BASE_URL; ?>juegos"<?php if (isType('juegos') || isType('versiones') || isType('ediciones')) echo 'class="selected"'; ?>>Juegos</a></li>
					<!--<li><a href="<?php echo BASE_URL; ?>sagas"<?php if (isType('sagas')) echo 'class="selected"'; ?>>Sagas</a></li>
					<li><a href="<?php echo BASE_URL; ?>plataformas"<?php if (isType('plataformas')) echo 'class="selected"'; ?>>Consolas</a></li>//-->
					<li><a href="<?php echo BASE_URL; ?>companias"<?php if (isType('companias')) echo 'class="selected"'; ?>>Compañías</a></li>
					<li><a href="<?php echo BASE_URL; ?>articulos"<?php if (isType('articulos')) echo 'class="selected"'; ?>>Artículos</a></li>
				</ul>
			</nav>
		</div>
	</header>