import React, { Component } from 'react';
import {ActivityIndicator,Button, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from '../shared/auth';
import {URL} from '../shared/const';
import ProjectButton from './ProjectButton';
import { setState } from '../shared/GlobalState';
import 'react-table/react-table.css';
import '../../index.css';

export default class UserList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		return fetch(URL + '/projects/' + this.props.project + '/users', {method:'GET', headers: getAuth()})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			console.error(error);
		});
	}

	showAddUser () {
		setState({
			isAuth: true,
			show: 'addUser',
			param: this.props.project
		});
	}

	render() {
		if (this.state.isLoading) {
			return (
				<View style={{flex: 1,padding: 20}}>
					<ActivityIndicator/>
				</View>
			)
		}

		const columns = [
			{
				Header: 'Name',
				accessor: '',
				Cell: props => <ProjectButton proj={props.value}/>
			}, {
				Header: 'Phone',
				accessor: '' // String-based value accessors!
			}
		]

		return (
			<View>
				<Button
					onPress = { this.showAddProject }
					title = "Add User"
					color = "#841584"
				/>
				<ReactTable data={this.state.dataSource} columns={columns}/>
			</View>
		);
	}
}
