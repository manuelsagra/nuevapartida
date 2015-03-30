<?php include('header.php'); ?>

<?php
if (!isset($_SESSION["genre"])) {
	$genres = getFullCombo(TYPE_GENRE);
	$_SESSION["genre"] = $genres;
} else {
	$genres = $_SESSION["genre"];
}
$attributes = ItemDAO::getAttributes($item->id);
$metadata = MetadataDAO::getMetadata($item->id);
?>

<article class="<?php echo $type->shortname; ?> clearfix">
	<header class="<?php echo getPlatformClass(); ?>">
		<div class="wrapper">
			<h1 title="<?php echo $item->name; ?>">
				<a href="<?php echo BASE_URL . "versiones/$item->parent_id" ?>"><?php echo $item->name; ?></a>
				<?php echo getPlatformLink(); ?>
			</h1>
			<?php if($item->altname && $item->altname != "") echo "<h2 title=\"$item->altname\">$item->altname</h2>" ?>
		</div>
	</header>
	<div class="wrapper full">
		<div class="clearfix">
			<a href="#" class="cover"><?php 
				$img = getItemImage($item->id, 512); 
				$img = ($img != '') ? $img : BASE_URL . 'img/defaultitem.png';
				echo '<img src="'. $img . '" alt="' . $item->name . '">';
			?></a>
			<section class="portlet info clearfix">
				<dl class="column">
					<dt>Fecha</dt>
					<dd><?php echo formatDate($item->date); ?></dd>
					<?php
					echo showAttribute('Género', TYPE_GENRE, 'showGenre');
					echo showAttribute('Desarrollado por', TYPE_DEVELOPED);
					echo showAttribute('Publicado por', TYPE_PUBLISHED);
					?>
				</dl>
				<dl class="column">
					<?php
					echo showAttribute('Región', TYPE_REGION, 'showRegion');
					echo showMetadata('Enlaces', TYPES_URL, 'showLink');
					echo showMetadata('Código de producto', TYPE_CATALOG);
					echo showMetadata('Código de Barras', TYPE_BARCODE, 'showBarcode');
					?>
				</dl>
			</section>
		</div>
	</div>
</article>

<?php include('footer.php'); ?>