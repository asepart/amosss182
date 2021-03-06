import React, { Component } from 'react';
import { Text, View } from 'react-native';
import { getAuth } from '../shared/auth';
import { URL } from '../shared/const';
import { isImage, isVideo } from '../Chat/ChatMessage';
import { ChatImage } from '../Chat/ChatImage';
import { ChatVideo } from '../Chat/ChatVideo';

export class File extends Component {
	
	//useless constructor warning
	/*
	constructor(props) {
		super(props);
	}
	*/

	deleteFile() {
		fetch(URL + '/tickets/' + this.props.name.ticketId + '/attachments/' + this.props.name.attachmentId, {
			method:'DELETE',
			headers: getAuth()
		})
		.then(window.location.reload())
		.catch((error) => {console.error(error);});
	}

	getPreview () {
		if (isImage(this.props.name.originalName)){
			return(
				<View>
					<ChatImage src={URL + "/files/" + this.props.name.attachmentId} />
				</View>
			);
		} else if (isVideo(this.props.name.originalName)) {
			return (
				<View>
					<ChatVideo src={URL + "/files/" + this.props.name.attachmentId} />
				</View>
			);
		} else {
			return (<View></View>);
		}
	}

	render() {
		return (
			<View style={{flexDireaction:'row'}}>
				<Text>
					<a href={URL + "/files/" + this.props.name.attachmentId}>{this.props.name.originalName}</a>
					{this.props.del === 'true' ?
						<img src={require('../images/delete.png')} onClick={this.deleteFile.bind(this)} style={{height: 15, width: 15, marginBottom: -5}} alt="delete"/>
					: <p>{this.getPreview()}</p>}
				</Text>
			</View>
		);
	}
}
