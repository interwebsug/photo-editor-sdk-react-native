declare module "photo-editor-sdk-react-native" {
  const TRANSFORM_TOOL: string;
  const FILTER_TOOL: string;
  const FOCUS_TOOL: string;
  const ADJUST_TOOL: string;
  const TEXT_TOOL: string;
  const STICKER_TOOL: string;
  const OVERLAY_TOOL: string;
  const BRUSH_TOOL: string;
  const DIVIDER: string;

  export function openEditor(imagePath: string, features: string[]): Promise<string>;
}