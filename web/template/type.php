<?php include('header.php'); ?>

<div class="clearfix">
	<?php
		$items = ItemDAO::getByType($type->id);
		showItems($items);
	?>
</div>

<?php include('footer.php'); ?>