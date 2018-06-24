export function logout () {
	document.cookie = 'username=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	document.cookie = 'psw=; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
	window.location.reload();
}
