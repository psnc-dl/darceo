var downloadMsgWait;
var downloadMsgReady;
var downloadMsgError;
var context;

var downloadTimer;

function downloadArchive(objectId, version) {
	$('#downloadFrame').empty();
	$('<iframe src="' + context + '/download?objectId=' + objectId + '&version=' + version + '"/>').appendTo('#downloadFrame');
}

function poll(objectId, version, requestId) {
	$.ajax({
		url: context + '/download?objectId=' + objectId + '&version=' + version + '&requestId=' + requestId,
		cache: false,
		dataType: 'text',
		success: function(data) {
			if (data == 'ready') {
				downloadReady(objectId, version);
			} else {
				downloadWait(objectId, version, requestId);
			}
		},
		error: function() {
			downloadError();
		}
	});
}

function downloadWait(objectId, version, requestId) {
	clearTimeout(downloadTimer);
	$('#downloadInfo').empty().text(downloadMsgWait);
	showDownloadPopup();
	downloadTimer = setTimeout(function() { poll(objectId, version, requestId); }, 5000);
}

function downloadReady(objectId, version) {
	clearTimeout(downloadTimer);
	$('#downloadInfo').empty();
	$('<a href="#" onclick="downloadArchive(\'' + objectId + '\', \'' + version + '\'); return false;">' + downloadMsgReady + '</a>').appendTo('#downloadInfo');
	showDownloadPopup();
}

function downloadError() {
	clearTimeout(downloadTimer);
	$('#downloadInfo').empty().text(downloadMsgError);
	showDownloadPopup();
}

function showDownloadPopup() {
	RichFaces.$('downloadPopup').show();
}

function hideDownloadPopup() {
	clearTimeout(downloadTimer);
	$('#downloadFrame').empty();
	RichFaces.$('downloadPopup').hide();
}
