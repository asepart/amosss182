export function getAuth () {
	// eslint-disable-next-line
	let username = 'user';
	// eslint-disable-next-line
	let password = 'passwd';
	let headers = new Headers();
	headers.append('Accept', 'application/json')
	//headers.append('Authorization', 'Basic ' + btoa(username + ":" + password));
	return (headers);
}
