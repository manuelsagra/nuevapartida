<?php
$months = ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"];
function formatDate($d) {
	global $months;
	if ($d == '0000-00-00') {
		return "Desconocida";
	} else if (substr($d, 5, 2) == '00') {
		return substr($d, 0, 4);
	} else if (substr($d, 8, 2) == '00') {
		return $months[intval(substr($d, 5, 2)) - 1] . ' ' . substr($d, 0, 4);
	} else {
		return intval(substr($d, 8, 2)) . ' ' . $months[intval(substr($d, 5, 2)) - 1] . ' ' . substr($d, 0, 4);
	}
}
function getYear($d) {
	return intVal(substr($d, 0, 4));
}
?>