import {Platform} from 'react-native';
import {URL} from '../Login/const';
import {getAuth, getAuthForMediaPost} from '../Login/auth';
import {setMsg, sendMessage} from './sendMessages';
import moment from 'moment';

export var type = '';

export function setType(asset) {
	type = asset;
}

export async function uploadFile (file, ticket) {
	var filename = '';
	var ext = '';

	if (Platform.OS === 'ios') {
		filename = file.filename;
	}
	else {
		if(type == 'Photos') {
			ext = '.jpg';
		}
		if(type == 'Videos') {
			ext = '.mp4';
		}
		filename = (btoa(moment()) + ext);
	}

	//convert file into FormData
	const image = {
					uri: file.uri,
					//type: 'multipart/form-data',
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
		//TODO: why does this not work for mp4?
		console.log("promise is: " + responseText)
		
		//send file url to chat
		fetch(URL + '/messages/' + ticket + '?attachment=' + responseText, {
					method: 'POST',
					headers: getAuth(),
					body: URL + '/files/' + responseText
		})
		.catch((error) => {
			console.log(error);
			alert("Sending attachment failed.");
		});
	})
	.catch((error) => {
		console.log(error);
		alert("File upload failed.");
	});

	console.log('FormData is: ' + JSON.stringify(imgBody));
}
