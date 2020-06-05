module.exports = {
  plugins: ['stylelint-scss'],
  extends: [
    'stylelint-config-recommended-scss',
    'stylelint-config-sass-guidelines'
  ],
  rules: {
    'max-nesting-depth': 4,
    'selector-max-compound-selectors': 4
  }
};
