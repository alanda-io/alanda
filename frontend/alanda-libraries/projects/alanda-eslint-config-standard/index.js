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
    'space-before-function-paren': [
      'error',
      'never'
    ],
    'max-len': [
      'error',
      {
        code: 120,
        comments: 120,
        ignoreUrls: true,
        ignoreTemplateLiterals: true,
        ignoreStrings: true
      }
    ],
    'dot-notation': [
      'off'
    ],
    '@typescript-eslint/semi': [
      'error',
      'always',
      {
        omitLastInOneLineBlock: true
      }
    ],
    '@typescript-eslint/space-before-function-paren': [
      'error',
      'never'
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
    '@angular-eslint/component-selector': [
      'warn',
      {
        type: 'element',
        prefix: 'alanda',
        style: 'kebab-case'
      }
    ]
  }
};
