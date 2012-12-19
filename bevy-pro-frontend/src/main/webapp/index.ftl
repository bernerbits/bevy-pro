<html>
	<head>
		<title>Bevy-Pro Beverage Dispenser</title>
		<h1>Bevy-Pro Beverage Dispenser</h1>
		<script type="text/javascript" src="./js/jquery-1.8.3.min.js"></script>
		<script type="text/javascript" src="./js/updater.js"></script>
		<link rel="stylesheet" type="text/css" href="./css/default.css"></link>
		<link rel="stylesheet" type="text/css" href="./css/effects.css"></link>
	</head>
	<body>
		<ul id="beverage-buttons">
			<#list m.bevs as bev>
				<li id="bev-${bev.id}" class="beverage-button <#if bev.soldOut>sold-out</#if>"><a href="./dispense/${bev.id}.ftl"><img src="${bev.imageUrl}" /></a></li>
			</#list>
		</ul>
		<div id="credit-div">Credit: <span id="credit-span">${m.creditString}</span></div>
		<div id="refund-button"><a href="./refund.ftl" class="<#if m.credit == 0>inactive</#if>">Refund</a></div>
		<div id="message-div">${m.message}</div>
	</body>
</html>