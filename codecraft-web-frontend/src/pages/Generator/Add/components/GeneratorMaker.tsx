import FileUploader from '@/components/FileUploader';
import { onlineMakeGeneratorUsingPost } from '@/services/backend/generatorController';
import { ProFormInstance } from '@ant-design/pro-components';
import { ProForm, ProFormItem } from '@ant-design/pro-form';
import { Alert, Button, Collapse, message } from 'antd';
import { saveAs } from 'file-saver';
import { useRef } from 'react';

interface Props {
  meta: any;
}

export default (props: Props) => {
  const { meta } = props;
  const formRef = useRef<ProFormInstance>();

  /**
   * 提交
   * @param values
   */
  const doSubmit = async (values: API.GeneratorMakeRequest) => {
    // 数据转换
    if (!meta.name) {
      message.error('请填写名称');
      return;
    }

    // 文件列表转 url
    const zipFilePath = values.zipFilePath;
    if (!zipFilePath || zipFilePath.length < 1) {
      message.error('请上传模板文件压缩包');
      return;
    }

    // 文件列表转换成 url
    // @ts-ignore
    values.zipFilePath = zipFilePath[0].response;

    try {
      const blob = await onlineMakeGeneratorUsingPost(
        {
          meta,
          zipFilePath: values.zipFilePath,
        },
        {
          responseType: 'blob',
        },
      );
      // 使用 file-saver 来保存文件
      saveAs(blob, meta.name + '.zip');
    } catch (error: any) {
      message.error('制作失败，' + error.message);
    }
  };

  /**
   * 表单视图
   */
  const formView = (
    <>
      <Alert
        message="在线制作功能只能够辅助生成，具体细节还需要调整，推荐下载生成器制作工具使用"
        type="warning"
        action={
          <Button
            size="small"
            type="primary"
            style={{ marginLeft: 8 }}
            onClick={() =>
              window.open('https://github.com/dingxinliang88/codecraft/tree/master/codecraft-maker')
            }
          >
            前往下载
          </Button>
        }
      />
      <div style={{ marginBottom: 16 }} />
      <ProForm
        formRef={formRef}
        submitter={{
          searchConfig: {
            submitText: '制作',
          },
          resetButtonProps: {
            hidden: true,
          },
        }}
        onFinish={doSubmit}
      >
        <ProFormItem label="模板文件" name="zipFilePath">
          <FileUploader
            biz="generator_make_template"
            description="请上传压缩包，打包时不要添加最外层目录！"
          />
        </ProFormItem>
      </ProForm>
    </>
  );

  return (
    <Collapse
      style={{
        marginBottom: 24,
      }}
      items={[
        {
          key: 'maker',
          label: '生成器制作工具',
          children: formView,
        },
      ]}
    />
  );
};
