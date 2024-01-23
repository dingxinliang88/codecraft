# ${name}

> ${description}
>
> Author: ${author}
>
> Based On [Code Craft](https://github.com/dingxinliang88/codecraft.git)

## How To Use

```shell
craft <command> <options>
```

e.g.
```shell
craft generate <#list modelConfig.models as modelInfo>-${modelInfo.abbr}</#list>
```

## Options

<#list modelConfig.models as modelInfo>
  ${modelInfo?index + 1}) ${modelInfo.fieldName}

  - type: ${modelInfo.type}
  - description: ${modelInfo.description}
  - defaultValue: ${modelInfo.defaultValue?c}
  - abbreviations: ${modelInfo.abbr}
</#list>