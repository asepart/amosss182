import React, { Component } from 'react';
import { Image } from 'react-native';

export default class ChatImage extends Component {
	viewImage (url){
		console.log('preview: ' + url);
	}
	render() {
		return (
			<Image
				source={require(this.props.children.preview)}
				style={{width: 100, height: 100}}
				onPress={this.viewImage(this.props.children.image)}
			/>
		);
	}
}
