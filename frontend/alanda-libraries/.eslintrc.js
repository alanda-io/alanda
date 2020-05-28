module.exports = {
  env: {
    browser: true,
    node: true
  },
  extends: 'standard',
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module'
  },
  rules: {
    'semi': [
      'error',
      'always',
      { omitLastInOneLineBlock: true }
    ]
  },
  overrides: [
    {
      files: ['*.ts'],
      parser: '@typescript-eslint/parser',
      parserOptions: {
        project: [
          'tsconfig.json',
          'tsconfig.app.json',
          'tsconfig.spec.json',
          'e2e/tsconfig.json',
          'projects/alanda-common/tsconfig.lib.json',
          'projects/alanda-common/tsconfig.spec.json'
        ]
      },
      plugins: [
        '@typescript-eslint',
      ],
      extends: [
        'standard-with-typescript',
        'plugin:@angular-eslint/recommended'
      ],
      rules: {
        '@typescript-eslint/semi': [
          'error',
          'always',
          { omitLastInOneLineBlock: true }
        ],
        '@angular-eslint/component-selector': [
          'warn',
          { type: 'element', prefix: 'alanda', style: 'kebab-case' },
        ],
        'max-len': [
          'off'
        ],
        'dot-notation': [
          'off'
        ]
      },
      overrides: [
        {
          files: ['*.component.ts'],
          plugins: ['@angular-eslint/template'],
          processor: '@angular-eslint/template/extract-inline-html'
        }
      ]
    }
  ],
};
