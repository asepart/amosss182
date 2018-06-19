import React, { Component } from 'react';
import { Image } from 'react-native';

export default class ChatVideo extends Component {
	viewVideo (url){
		console.log('preview: ' + url);
	}
	render() {
		return (
			<Image
				source={require(this.props.children.preview)}
				style={{width: 100, height: 100}}
				onPress={this.viewVideo(this.props.children.image)}
			/>
		);
	}
}
