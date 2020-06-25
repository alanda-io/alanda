module.exports = {
  name: 'config',
  preset: '../../jest.config.js',
  coverageDirectory: '../../coverage/libs/config',
  snapshotSerializers: [
    'jest-preset-angular/build/AngularNoNgAttributesSnapshotSerializer.js',
    'jest-preset-angular/build/AngularSnapshotSerializer.js',
    'jest-preset-angular/build/HTMLCommentSerializer.js',
  ],
};
