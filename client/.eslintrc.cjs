module.exports = {
    root: true,
    env: {
      browser: true,
      es2020: true,
    },
    extends: [
      'eslint:recommended',
      'plugin:react/recommended',
      'plugin:react/jsx-runtime',
      'plugin:react-hooks/recommended',
      'airbnb',
      'plugin:@typescript-eslint/strict-type-checked',
      // 'plugin:@typescript-eslint/stylistic-type-checked', // TODO: включить, когда будет готова основная кодовая база
    ],
    ignorePatterns: ['dist', '.eslintrc.cjs', 'build'],
    parser: '@typescript-eslint/parser',
    parserOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      projectService: true,
      project: './tsconfig.json',
    },
    settings: {
      'import/parsers': {
        '@typescript-eslint/parser': ['.ts', '.tsx']
      },
      'import/resolver': {
        'typescript': {},
      },
      react: { version: '19' } },
    plugins: ['react-refresh', '@typescript-eslint'],
    rules: {
      'max-len': ['error', {'code': 108 }],
      'no-console': 'off', // FIX: убрать, когда бОльшая часть кода будет готова
      'react/jsx-no-target-blank': 'off',
      'react/react-in-jsx-scope': 'off',
      'react/jsx-one-expression-per-line': 'off',
      'import/extensions': 'off',
      'import/no-anonymous-default-export': 'off',
      'import/no-unresolved': 'off', // FIX: если убрать это правило, то eslint начинает ругаться на TS алиасы (причём, возможно, только в VS Code). Надо пофиксить
      'object-curly-newline': ['error', {
        multiline: true,
        minProperties: 5,
        consistent: true,
      }],
      'react/function-component-definition': [
        'error',
        {
          'namedComponents': 'arrow-function',
          'unnamedComponents': 'arrow-function',
        }
      ],
      'jsx-a11y/label-has-associated-control': 'off',
      'react/jsx-filename-extension': [0, { 'extensions': ['.tsx'] }],
      '@typescript-eslint/no-non-null-assertion': 'off',
      '@typescript-eslint/no-confusing-void-expression': 'off',
      '@typescript-eslint/no-misused-promises': 'off',
      '@typescript-eslint/unbound-method': 'off',
    },
    overrides: [
      {
        files: ['**/*.js', '**/*.jsx'],
        extends: ['plugin:@typescript-eslint/disable-type-checked'],
      },
      {
        files: ['./vite.config.ts', './app/routes.ts', './app/src/api/index.ts'],
        rules: {
          'import/no-extraneous-dependencies': 'off',
        },
      },
    ]
  }
