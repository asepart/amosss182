import React, { Component } from 'react';
import CPopup from '../shared/Popup';

export default class ChatImage extends Component {
	render() {
		return (
			<CPopup toggle="<img source={this.props.children.image}?thumbnail=true' style={{width: 100, height: 100}}/>">
				<img source={this.props.children.image} alt=""/>
			</CPopup>
		);
	}
}
