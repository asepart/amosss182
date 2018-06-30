import {URL} from '../Login/const';
import {getAuth, getAuthForMediaPost} from '../Login/auth';
import {setMsg, sendMessage} from './sendMessages';
//import fileType from 'react-native-file-type';
//import FileSystem from 'react-native-filesystem';
import moment from 'moment';

var link = '';

export function uploadFile (uri, ticket) {
	
	//TODO: import fileType correctly, then enable commented code
	
	//create new filename
	var ext = '';
/*		fileType(this.state.selected[0].uri).then((type) => {
	    //Ext: type.ext
	    //MimeType: type.mime
		ext = type.ext;
	})
	var filename = (moment().format() + '.' + ext);
*/	var filename = moment().format();
	
	//send  filename to chat
	var tmp = new Date();
	var date = tmp.toDateString();
	var time = tmp.toTimeString().slice(0,8);
	var timestamp = "[" + date + ", " + time + "]";
	setMsg(timestamp + ": " + filename);
	sendMessage();
	
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
	}).then(
		    response => {
		    	response.json();
		    	if (response.status == '409') {
		    		alert("File upload failed. Filename already exists.");
		    	}
		    }
	  ).catch(
	    error => {
	    	console.log('uploadImage error:', error);
	    	alert("File upload failed.");
	    }
	  );
	
	console.log(ticket);
	console.log(imgBody);
}

export function downloadFile (filename, ticket) {
	
	//get downloadlink from backend
	fetch(URL + '/files/' + ticket + '/' + filename + '?thumbnail=false', {
        method: 'GET',
        headers: getAuth()
	}).then(
		    response => {
		    	response.json();
		    	link = '';
		    	if (response.status == '200') {
		    		link = response.url;
		    	} else if (response.status == '404') {
		    		alert("File download failed. File does not exist.");
		    	}
		    }
	  ).catch(
	    error => {
	    	console.log('downloadImage error:', error);
	    	alert("File download failed.");
	    }
	  );
	
	//TODO: implement file download and storage
	if (link !== '') {
		
		console.log(link);
		
/*		const { uri: localUri } = FileSystem.downloadAsync(
				  link,
				  FileSystem.documentDirectory + filename
				)
				  .then(({ uri }) => {
				    console.log('Finished downloading to ', uri);
				  })
				  .catch(error => {
				    console.error(error);
				  });
*/		
		alert("Saved " + filename);
	}
}