module.exports = {
  extends: [
    'standard-with-typescript',
    'plugin:@angular-eslint/recommended'
  ],
  parserOptions: {
    ecmaVersion: 2020,
    sourceType: 'module',
    project: [
      'tsconfig.app.json',
      'tsconfig.spec.json',
      'e2e/tsconfig.json',
      'projects/alanda-common/tsconfig.lib.json',
      'projects/alanda-common/tsconfig.spec.json'
    ]
  },
  rules: {
    '@typescript-eslint/semi': [
      'error',
      'always',
      { "omitLastInOneLineBlock": true }
    ],
    // ORIGINAL tslint.json -> "component-selector": [true, "element", "app", "kebab-case"],
    '@angular-eslint/component-selector': [
      'warn',
      { type: 'element', prefix: 'alanda', style: 'kebab-case' },
    ],
    'max-len': [
      'warn'
    ]
  },
  overrides: [
    {
      files: ['*.component.ts'],
      parser: '@typescript-eslint/parser',
      parserOptions: {
        ecmaVersion: 2020,
        sourceType: 'module',
      },
      plugins: ['@angular-eslint/template'],
      processor: '@angular-eslint/template/extract-inline-html',
    },
  ],
};
