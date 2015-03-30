<?php include('header.php'); ?>

<article class="<?php $type->shortname?> clearfix">
	<header>
		<div class="wrapper">
			<h1 title="<?php echo $item->name; ?>"><?php echo $item->name; ?></h1>
			<?php if($item->altname && $item->altname != "") echo "<h2>$item->altname</h2>" ?>
		</div>
	</header>
	<div class="wrapper full">
		<div class="content">
			<?php echo $item->content; ?>
		</div>
	</div>
</article>
<div class="wrapper">
<?php 			
	$items = ItemDAO::getGames($item->id);
	showItems($items);
?>
</div>

<?php include('footer.php'); ?>