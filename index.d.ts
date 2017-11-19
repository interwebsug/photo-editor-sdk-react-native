declare module "photo-editor-sdk-react-native" {
  const transformTool: any;
  const filterTool: any;
  const focusTool: any;
  const adjustTool: any;
  const textTool: any;
  const stickerTool: any;
  const overlayTool: any;
  const brushTool: any;
  const magic: any;

  export function openEditor(imagePath: string, features: any[], options: object): Promise<string>;
  export function openCamera(features: any[], options: object): Promise<string>;
}
