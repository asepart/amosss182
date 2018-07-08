import React, {Component} from 'react';
import {
  AppRegistry,
  Dimensions,
  StyleSheet,
  Text,
  Image,
  TouchableOpacity,
  TouchableHighlight,
  View
} from 'react-native';
// import styles from '../Login/Design';
import { RNCamera } from 'react-native-camera';
import { uploadFile } from '../Chat/files';
import { ticket } from '../Chat/sendMessages';
import Icon from 'react-native-vector-icons/FontAwesome';

export var uri = '';

export default class CameraAccess extends Component { 

  constructor(props) {
    super(props);
    this.state = {
      path: null,
      type: 'back'
    };
  }

  toggleFacing() {
    this.setState({
      type: this.state.type === 'back' ? 'front' : 'back',
    });
}

renderCamera() {
  return (
    <View style={styles.container}>
    <RNCamera
        ref={ref => {
          this.camera = ref;
        }}
        style = {styles.preview}
        type={this.state.type}
        flashMode={RNCamera.Constants.FlashMode.auto}
        permissionDialogTitle={'Permission to use camera'}
        permissionDialogMessage={'We need your permission to use your camera phone'}
    >
    <View
          style={{
            flex: 0.2,
            backgroundColor: 'transparent',
            flexDirection: 'row',
            justifyContent: 'space-around',
            alignSelf: 'flex-end',
          }}
    >
    <TouchableOpacity style={styles.flipButton} onPress={this.toggleFacing.bind(this)}>
      <Icon name="user" size={30} color="#FFF"/>
    </TouchableOpacity>
    <TouchableHighlight
      style={ styles.capture }
      onPress={this.takePicture.bind(this)}
      underlayColor="rgba(255, 255, 255, 0.5)"
    >
      <Icon name="camera" size={30} color="#FFF"/>
    </TouchableHighlight>
    </View>
    </RNCamera>
  </View>
  );
}

renderImage() {
  return (
    <View style={styles.container}>
      <Image
        source={{ uri: this.state.path }}
        style={styles.preview}
      />
      <Text
        style={styles.cancel}
        onPress={() => this.setState({ path: null })}
      >Cancel
      </Text>
      <TouchableHighlight
      style={ styles.capture }
      onPress={uploadFile(this.state.path, ticket)}
      underlayColor="rgba(255, 255, 255, 0.5)"
    >
      <Icon name="cloud-upload-alt" size={30} color="#FFF"/>
    </TouchableHighlight>
    </View>
  );
}

render() {
  return (
    <View style={styles.container}>
      {this.state.path ? this.renderImage() : this.renderCamera()}
    </View>
  );
}

  takePicture = async function() {
    try {
      console.log(" ========================== takePicture =================================");
      if (this.camera) {
        const options = { quality: 0.5 };
        data = await this.camera.takePictureAsync(options);
        this.setState({ path: data.uri });
        // uploadFile(data.uri, ticket);
      };
    } catch (err) {
      console.log('err: ', err);
    }
  };
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#000',
  },
  flipButton: {
    width: 70,
    height: 70,
    borderRadius: 10,
    borderWidth: 2,
    alignItems: 'center',
    justifyContent: 'center',
    borderColor: '#FFF',
    left: 10,
    flex: 0.1,
    alignSelf: 'flex-end'
  },
  preview: {
    flex: 1,
    justifyContent: 'flex-end',
    alignItems: 'center',
    height: Dimensions.get('window').height,
    width: Dimensions.get('window').width
  },
  capture: {
    width: 70,
    height: 70,
    borderRadius: 35,
    borderWidth: 5,
    borderColor: '#FFF',
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'flex-end'
    // marginBottom: 10,
  },
  send: {
    width: 70,
    height: 70,
    borderRadius: 35,
    borderWidth: 5,
    borderColor: '#FFF',
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 10,
  },
  cancel: {
    backgroundColor: 'transparent',
    color: '#FFF',
    fontWeight: '600',
    fontSize: 17,
    marginBottom: 20,
    left:20
  }
});
