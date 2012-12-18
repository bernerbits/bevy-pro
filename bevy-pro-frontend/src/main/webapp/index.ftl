<html>
	<head>
		<title>Bevy-Pro Beverage Dispenser</title>
		<h1>Bevy-Pro Beverage Dispenser</h1>
		<script type="text/javascript" src="./js/jquery-1.8.3.min.js"></script>
		<link rel="stylesheet" type="text/css" href="./css/default.css"></link>
	</head>
	<body>
		<ul id="beverage-buttons">
			<#list bevs as bev>
				<li id="bev-${bev.id}" class="beverage-button"><a href="./dispense/${bev.id}.ftl"><img src="${bev.imageUrl}" /></a></li>
			</#list>
		</ul>
		<div id="credit-div">Credit: <span id="credit-span">${credit}</span>&cent;</div>
		<div id="message-div">${message}</div>
	</body>
</html>