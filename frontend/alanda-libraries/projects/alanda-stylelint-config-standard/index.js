module.exports = {
  plugins: ['stylelint-scss'],
  extends: [
    'stylelint-config-standard',
    'stylelint-config-sass-guidelines'
  ],
  rules: {
    'max-nesting-depth': 4,
    'selector-max-compound-selectors': 4
  }
};
