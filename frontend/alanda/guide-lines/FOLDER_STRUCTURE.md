# Guidelines

see [Styleguide - Symbols and File names](https://angular.io/guide/styleguide#symbols-and-file-names)

## Why?

Consistency throughout a project or even a team(!) helps

- when looking for a specific piece of code
- provides a seamless pattern matching for any automated tasks
- easy localisation of errors
  This speeds up development and helps to orientate in the codebase.

##### Filenames - Do's & Dont's

`addressdetailviewservice.ts`: Could be used without a problem, but we are humans, we like any kind of separation between words. Dyslexia sends its regards!

`address-detail-view.service.ts`: Very nice! You can easily separate the words without flexing your brain muscles too much.

Not only with the use of `-`es does the readability of filenames increases drastically, the use of `.`s helps with differentiating between types of files (`.service.ts`, `.module.ts`, etc).

**Avoid abbreviations!**, first and foremost within type names, they _can_ and _will_ be confusing (`.srv.ts`, `.svc.ts` ==> `.service.ts`).

## Prefixes

Prefixes are the key to a well-structured project.

Every `feature/ui/etc` has its own subfolder under its corresponding main-folder, where everything concerning a `feature / ui / etc`-module is located. <br>
e.g. `features/merge/`, `ui/src/lib/address/`, etc.

A `feature` module needs the prefix `feature` (`feature-merge.module.ts`) to identify as such.
Inside e.g. a `feature` directive, the underlying components don't need the prefix `feature`.

## index.ts / angular.json / nx.json

### index.ts

To shorten paths and get a nice and clean import section, we use `index.ts` files (barrel files) within `features / ui / etc`.

All imports which are referenced in an `index.ts` get a short import path like `import { ... } from '@oekotex/data-access';`.

These `index.ts` files have to be registered in the `tsconfig.json` on root level.

### angular.json && nx.json

In `nx.json` under `projects`, you tag a `feature` as type `feature`:

```json
  {
    "features-merge": {
          "tags": ["type:feature"]
        }
  }
```

`Features` need to be declared as such in angular.json e.g. `features-[name]`.

The name you use for your `feature` in `angular.json` has to be the same in `nx.json`.

## General Folder structures

**Flat**

any-component/  
├── any-component.component.ts  
└── any-component.component.spec.ts

A flat structure is preferred if the components complete code (scss, html included) is under 150 lines.
Furthermore, we can move out e.g. html or css if these sections are bigger than 80 lines.

**Separated**

any-component/  
├── any-component.component.scss  
├── any-component.component.html  
├── any-component.component.ts  
└── any-component.component.spec.ts

A separated structure is preferred if we have big components.
This also comes in handy if we compost multiple css files into one or we have to maintain multiple versions of html.

### Display Component

any-component/  
├── any-component.component.scss  
├── any-component.component.html  
├── any-component.component.ts  
├── any-component.component.spec.ts  
├── any-component.presenter.ts  
├── any-component.validators.ts  
├── any-component.[subjcet-name].model.ts  
├── [subjcet-name].model.ts  
├── any-component.utils.ts  
└── index.ts

A Display component only relies on input and outputs. It contains the logic to display the passed data.

- If the logic responsible to organize the view is too big we move it into a `any-component.presenter.ts`.
  It is created as a service class.
  The persenter is instanciated once per component, done over the DI of manually. If it is used, the view (html) only referes to the presenters reference.
  All other variables need to be contained in the presenter.
- To keep the component in question structured and focused we move interfaces, enums, types type guards etc. into a separate file `[subjcet-name].model.ts`.
  This could be a file for the components model `any-component.model.ts` or view model `any-component.presenter.model.ts`, ore a specific type used in a component e.g. `address.model.ts`
- Container maintaining a form can hold it's local validation in a `any-component.validators.ts` file
- Functions like transforms, helpers etc go into `any-component.utils.ts` if they grow too big.

### Container Component

any-container/  
├── any-container.component.scss  
├── any-container.component.html  
├── any-container.component.ts  
├── any-container.component.spec.ts  
├── any-container.facade.ts  
├── any-container.[subject-name].adapter.ts  
├── any-container.[subjcet-name].model.ts
├── [subjcet-name].model.ts
├── any-container.utils.ts  
└── index.ts

A container component orchestrated the related remote sources (global state or http etc and general business logic)

- The containers `any-container.component.html` mostly contains layouting like grids etc and child componets.
- The containers `any-container.component.scss` should focus on layout and structure.
- The containers `any-container.component.ts` file contains business logic, and instances of the containers adapters/facades

If the additional logic not related to the components template handling is big we can move it into a facade or adapter.

- The `any-container.facade.ts` includes basically every thing else. A facade primarly is a wrapper for a coplex API to expose a more simpler one to the component.
  It is created as a service class.
- The `any-container.[subject-name].adapter.ts` is a more specific file. An adapter adapts one interface to another e.g. router state to the container state or server models to client models.
  It is created as a service class.
- the `model` and `utils` files serve the same purpose as they do in the display component.

### Directives

any-directive/  
├── any-directive.directive.ts  
├── any-directive.directive.spec.ts
├── any-directive.[subjcet-name].model.ts
├── [subjcet-name].model.ts
├── any-directive.utils.ts
└── index.ts

### Pipes

any-pipe/  
├── any-pipe.pipe.ts  
├── any-pipe.pipe.spec.ts
├── any-pipe.[subjcet-name].model.ts
├── [subjcet-name].model.ts
├── any-pipe.utils.ts  
└── index.ts

### Service

any-service/  
├── any-service.service.ts  
└── any-service.service.spec.ts

### InjectionToken

any-token/  
├── any-token.token.ts  
└── any-token.token.spec.ts

### Libs

libs/
├── shared-group-folder-1/
│ ├── shared-lib-1/
│ │ ├── shared-module-1/
│ │ └── shared-module-2/
│ └── shared-lib-2/
│ └── shared-module-3/
└── feature-group-folder-1/
├── feature-lib-1/
│ ├── feature-module-1/
│ └── feature-module-2/
└── feature-lib-2/
├── feature-module-1/
└── feature-module-2/
  
Lib types:

- util
- shared
- feature

#### Shared Module

any-shared-module/
├── component-name/
├── directive-name/
├── pipe-name/
├── any-shared-module.module.ts  
├── any-shared-module.module.spec.ts
└── index.ts

#### Feature Module

any-feature-module/
├── component-name/
├── directive-name/
├── pipe-name/
├── any-feature-module.module.ts  
├── any-feature-module.module.spec.ts
├── any-feature-module.routing.ts
├── any-feature-module.menu.ts  
├── eager-index.ts  
└── index.ts

### App

apps/
├── app-1/
└── app-2/
