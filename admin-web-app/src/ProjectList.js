import React, { Component } from 'react';
import {Button, ActivityIndicator, View} from 'react-native';
import ReactTable from 'react-table';
import {getAuth} from './auth';
import {URL} from './const';
import 'react-table/react-table.css';
import './index.css';

export class ProjectList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			isLoading: true
		}
	}

	componentDidMount() {
		let headers = getAuth();
		return fetch(URL + '/projects', {method:'GET', headers: headers})
		.then((response) => response.json())
		.then((responseJson) => {
			this.setState({
				isLoading: false,
				dataSource: responseJson
			}, function() {});
		}).catch((error) => {
			alert("sad");
			console.error(error);
		});
	}

	render() {
		if (this.state.isLoading) {
			return (<View style={{
					flex: 1,
					padding: 20
				}}>
				<ActivityIndicator/>
			</View>)
		}

		const columns = [
			{
				Header: 'Project Name',
				accessor: 'projectName' // String-based value accessors!
			}, {
				Header: 'Entrycode',
				accessor: 'entryKey' // String-based value accessors!
			}
		]

		return (<View>
			<Button onPress="" title="Add Project" color="#841584"/>
			<ReactTable data={this.state.dataSource} columns={columns}/>
		</View>);
	}
}
