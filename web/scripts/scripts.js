var search = debounce(searchRaw, 1000);v
var d, p;

function bind(ctx, fn) {
	return function() {
		return fn.apply(ctx, arguments);
	}
}
function debounce(fn, time) {
	var timerId;
	return function() {
		var args = arguments;
		if (timerId) clearTimeout(timerId);
		timerId = setTimeout(bind(this, function() {
			fn.apply(this, args);
		}), time);
	}
}

function showLoading() {
	$('<div id="overlay"></div>').appendTo("body").fadeIn('fast');
}

function hideLoading() {
	$("#overlay").fadeOut('slow', function() {
		$(this).remove();
	});
}





$(document).ready(function() {
	var minYear = 1988;
	var maxYear = 2008;
	$('#yearsSlider>div').slider({ 
		range: true,
		values: [minYear, maxYear],
		min: minYear,
		max: maxYear,
		stop: function() {
			search();
		},
		slide: function(event, ui) {
			$('#yearFrom').val(ui.values[0]);
			$('#yearTo').val(ui.values[1]);
		}
	});
	$('#yearFrom').val(minYear);
	$('#yearTo').val(maxYear);
	$('.text').keyup(function() {
		search();
	});
	$('select.combo').change(function() {
		search();
	});
	$('.year input').change(function() {
		search();
		var yearFrom = $('#yearFrom').val();
		var yearTo = $('#yearTo').val();
		if(isNaN(parseInt(yearFrom)) || !isFinite(yearFrom) || parseInt(yearFrom) < minYear) {
			yearFrom = minYear;
		}		
		if(isNaN(parseInt(yearTo)) || !isFinite(yearTo) || parseInt(yearTo) > maxYear) {
			yearTo = maxYear;
		}
		if (yearTo < yearFrom) {
			var aux = yearTo;
			yearTo = yearFrom;
			yearFrom = aux;
		}
		$('#yearFrom').val(yearFrom);
		$('#yearTo').val(yearTo);
		$('#yearsSlider>div').slider('values', [yearFrom, yearTo]);
	});
	if($('#s').length > 0) {
		searchRaw();
	}
	$('select.combo').each(function(k, v) {
		loadCombo(v.id);
	});
});
function updateYears() {
	$('#yearFrom').val($('#yearsSlider>div').slider('values', 0));
	$('#yearTo').val($('#yearsSlider>div').slider('values', 1));
}
function searchRaw() {
	var criteria = {
		type: $('#type').val(),
		s: $('#s').val(),
		code: $('#code').val(),
		from: $('#yearFrom').val(),
		to: $('#yearTo').val()
	};
	$('.combo').each(function(k, v) {
		criteria[v.id] = v.value;
	});
	showLoading();
	$.ajax({
		url: BASE_URL + 'search.php',
		data: criteria,
		success: function(data) {
			p = 0;
			d = data;
			$('.content').html('<section class="portlet results"><header><h1>Resultados</h1></header><div class="charts clearfix"></div><footer></footer></section>');
			$('#results').html('<ul class="items clearfix"></ul>');
			loadData();			
		},
		error: function() {
			alert('ERROR');
		}
	});
}
function loadCombo(id) {
	$.ajax({
		url: BASE_URL + 'combo.php',
		data: {
			c: id
		},
		cache: true,
		success: function(data) {
			$('#' + id + ' option.loading').remove();
			if (data && data.length > 0) {
				if (!data[0].g) {
					$.each(data, function(k, v) {
						$('#' + id).append('<option value="' + v.i + '">' + v.n + '</option>');
					});
				} else {
					var oldGrp = '', opt = '';
					for (var i = 0; i < data.length; i++) {
						var v = data[i];
						if (oldGrp != v.g) {
							opt += (opt != '' ? '</optgroup>' : '') + '<optgroup label="' + v.g + '">';
						}
						opt += '<option value="' + v.i + '">' + v.n + '</option>';
						oldGrp = v.g;
					}
					$('#' + id).append(opt + '</optgroup>');					
				}
			}
		},
		error: function() {
			$('#' + id + ' option.loading').text('Error cargando valores');
		}
	});
}
function loadData() {
	if(google) {
		google.load('visualization', '1.0', {
			packages: ['corechart'],
			callback: function() {
				showCharts();
				showResults();
				hideLoading();
			}
		} )
	}
}
function showCharts() {
	$('.portlet footer').html(d.results.length + ' resultado' + (d.results.length != 1 ? 's' : ''));

	if (d.years) {
		$('.charts').append('<div id="years"></div><div id="platforms"></div><div id="regions"></div><div id="genres"></div>');
		
		if (d.years) {
			var data = getDataTable(d.years, 'Año', 'Lanzamientos')		
			var chart = new google.visualization.LineChart(document.getElementById('years'));
			chart.draw(data, {
				legend: 'none',
				curveType: 'function',
				width: 400,
				height: 200,
				title: 'Distribución por años',
				vAxis: {					
					viewWindowMode: 'explicit', 
					viewWindow:{ 
						min: 0 
					},
					format: '###',
					gridlines: {
						count: 2
					}
				}
			});
		}
		if (d.platforms) {
			var data = getDataTable(d.platforms, 'Plataforma', 'Lanzamientos')		
			var chart = new google.visualization.PieChart(document.getElementById('platforms'));
			chart.draw(data, {
				legend: 'none',
				curveType: 'function',
				width: 200,
				height: 200,
				title: 'Distribución por plataformas'
			});
		}
		if (d.regions) {
			var data = getDataTable(d.regions, 'Región', 'Lanzamientos')		
			var chart = new google.visualization.PieChart(document.getElementById('regions'));
			chart.draw(data, {
				legend: 'none',
				curveType: 'function',
				width: 200,
				height: 200,
				title: 'Distribución por regiones'
			});
		}
		if (d.genres) {
			var data = getDataTable(d.genres, 'Género', 'Lanzamientos')		
			var chart = new google.visualization.PieChart(document.getElementById('genres'));
			chart.draw(data, {
				legend: 'none',
				curveType: 'function',
				width: 200,
				height: 200,
				title: 'Distribución por géneros'
			});
		}
	}
}
function getDataTable(da, x, y) {
	var data = new google.visualization.DataTable();
	data.addColumn('string', x);
	data.addColumn('number', y);
	var rows = [];
	$.each(da, function(k, v) {
		rows.push([k, v]);
	});
	data.addRows(rows);
	return data;
}
function showResults() {
	var r = '';
	var end = p + 40;
	var i = p;
	while (i < d.results.length && i < end) {
		var v = d.results[i];
		r += '<li style="display: none"><a href="' + BASE_URL + d.type + '/' + v.i + '" style="background-image:url(' + BASE_URL + 'getItemImg.php?id=' + v.i + ')"><span>' + v.n + '</span></a></li>';
		i++;
	}
	p = i;
	$('a.more').remove();
	if (p < d.results.length) {
		$('ul.items').after('<a href="#" class="more">Mostrar más</a>');
		$('a.more').click(function (e) {
			e.preventDefault();
			showResults();
		});
	}
	$('.items').append(r);
	$('.items li:hidden').fadeIn('fast');
}
