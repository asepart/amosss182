import React, {Component} from 'react';
import {View} from 'react-native';
import '../../index.css';
import Popup from "reactjs-popup";

export default class CPopup extends Component {
	constructor(props) {
		super(props);
		this.state = {
			open: false,
		};
	}
	openPopup = () => {
		this.setState({ open: true });
	};
	closePopup = () => {
		this.setState({
		 	open: false,
		})
	};

	render() {
		return(
			<View>
				<div onClick={this.openPopup}>
					{ this.props.toggle }
				</div>
				<Popup
					open={this.state.open}
					closeOnDocumentClick
					onClose={this.closePopup}
				>
					{this.props.children}
				</Popup>
			</View>
		);
	}
}
