module.exports = {
  extends: [
    'standard',
    'standard-with-typescript',
    'plugin:@angular-eslint/recommended'
  ],
  rules: {
    semi: [
      'error',
      'always',
      {
        omitLastInOneLineBlock: true
      }
    ],
    '@typescript-eslint/semi': [
      'error',
      'always',
      {
        omitLastInOneLineBlock: true
      }
    ],
    '@typescript-eslint/member-delimiter-style': [
      'error',
      {
        multiline: {
          delimiter: 'semi',
          requireLast: true
        },
        singleline: {
          delimiter: 'semi',
          requireLast: false
        }
      }
    ],
    '@typescript-eslint/space-before-function-paren': [
      'error',
      'never'
    ],
    '@angular-eslint/component-selector': [
      'warn',
      {
        type: 'element',
        prefix: 'alanda',
        style: 'kebab-case'
      }
    ],
    'max-len': [
      'off'
    ],
    'dot-notation': [
      'off'
    ]
  }
};
