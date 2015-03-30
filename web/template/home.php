<?php include('header.php'); ?>

<?php /*
<aside>
		<section class="portlet">
			<header>
				<h1>Juegos populares</h1>
			</header>
			<?php
				$games = ItemDAO::getPopularItemsByType(TYPE_GAME);
				if (is_array($games)) {
					echo "<ul class=\"references\">";
					foreach($games as $game) {
						$bg = getItemImage($game->id, 64);
						$bg = ($bg != '') ? $bg : BASE_URL . 'img/defaultitem.png';
						echo "<li class=\"clearfix\" title=\"$game->name\">";
						echo "<a href=\"" . getItemLink($types[TYPE_GAME], $game) . "\" style=\"background-image:url($bg)\">";
						echo "<span class=\"name\">$game->name</span>";
						$year = substr($game->date, 0, 4);
						$year = ($year != "0000") ? "$year" : "";
						echo "<span class=\"data\">$year</span>";
						echo "</a></li>";
					}
					echo '</ul>';
				}
			?>
		</div>
		</section>
</aside>
*/ ?>

<?php include('footer.php'); ?>