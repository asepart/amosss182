import {Platform} from 'react-native';
import {URL} from '../Login/const';
import {getAuth, getAuthForMediaPost} from '../Login/auth';
import {setMsg, sendMessage} from './sendMessages';
import moment from 'moment';

var link = '';

export var type = '';

export function setType(asset) {
	type = asset;
}

export async function uploadFile (uri, ticket) {

	var ext = '';
	if(type == 'Photos') {
		ext = '.jpg';
	}
	if(type == 'Videos') {
		ext = '.mp4';
		if (Platform.OS === 'ios') {
			ext = '.MOV';
		}
	}

	var filename = (btoa(moment()) + ext);

	//convert file into FormData
	const image = {
					uri: uri,
					type: 'multipart/form-data',
					name: filename
	}
	const imgBody = new FormData();
	imgBody.append('file', image);

	//send file to backend
	fetch(URL + '/files/' + ticket, {
				method: 'POST',
				headers: getAuthForMediaPost(),
				body: imgBody
	})
	.then((response) => {
		return response.text()
	})
	.then((responseText) => {
		console.log(URL + '/files/' + ticket)
		console.log("promise is: " + responseText)
		//send file url to chat

		fetch(URL + '/messages/' + ticket + '?attachment=' + responseText, {
					method: 'POST',
					headers: getAuth(),
					body: URL + '/files/' + responseText
		})
	})
	.catch((error) => {
		console.log(error);
		alert("File upload failed.");
	});

	console.log(ticket);
	console.log(imgBody);
}
