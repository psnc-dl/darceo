<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<noscript>
		<meta http-equiv="refresh" content="0;URL='${it.url}?js=false'"/>
	</noscript>
	<title>Detecting plugins...</title>
	<script type="text/javascript" src="${it.baseUrl}resources/PluginDetect.js"></script>
</head>
<body>
	<script type="text/javascript">
		var url = "${it.url}?js=true";

		function displayPDFresults(){

			var plugins = new Array();

			if (PluginDetect.isMinVersion('PDFReader', 0) >= 0) {
				plugins.push("PDF");
			}

			if (PluginDetect.isMinVersion('Flash', 0) >= 0) {
				plugins.push("Flash");
			}

			if (PluginDetect.isMinVersion('QuickTime', 0) >= 0) {
				plugins.push("QuickTime");
			}

			if (PluginDetect.isMinVersion('RealPlayer', 0) >= 0) {
				plugins.push("RealPlayer");
			}

			if (PluginDetect.isMinVersion('Silverlight', 0) >= 0) {
				plugins.push("Silverlight");
			}

			if (PluginDetect.isMinVersion('WindowsMediaPlayer', 0) >= 0) {
				plugins.push("WMP");
			}

			if (PluginDetect.isMinVersion('Java', 0) >= 0) {
				plugins.push("Java");
			}

			for (i = 0 ; i < plugins.length; i++) {
				url += "&plugins=" + plugins[i];
			}

			window.location = url;
		}

		PluginDetect.onDetectionDone('PDFReader', displayPDFresults, "${it.baseUrl}resources/empty.pdf");
	</script>
</body>
</html>