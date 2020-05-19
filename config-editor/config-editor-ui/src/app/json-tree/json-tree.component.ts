import { Component, Input, OnChanges } from '@angular/core';
import { PopupService } from '@app/popup.service';

export interface Segment {
  key: string;
  value: any;
  type: undefined | string;
  description: string;
  expanded: boolean;
  path: string;
}

@Component({
  selector: 're-json-tree',
  templateUrl: './json-tree.component.html',
  styleUrls: ['./json-tree.component.scss'],
})
export class JsonTreeComponent implements OnChanges {

  @Input() json: any;
  @Input() expanded = true;
  @Input() prevKey: string;
  @Input() cleanOnChange = true;

  segments: Segment[] = [];

  constructor(private snackbar: PopupService) {}

  ngOnChanges() {
    if (this.cleanOnChange) {
      this.segments = [];
    }
    if (typeof this.json === 'object') {
      Object.keys(this.json).forEach( key => {
        this.segments.push(this.parseKeyValue(key, this.json[key]));
      });
    } else {
      this.segments.push(this.parseKeyValue(`(${typeof this.json})`, this.json));
    }
  }

  isExpandable(segment: Segment) {
    return segment.type === 'object' || segment.type === 'array';
  }

  toggle(segment: Segment) {
    if (this.isExpandable(segment)) {
      segment.expanded = !segment.expanded;
    }
  }

  private parseKeyValue(key: any, value: any): Segment {
    const segment: Segment = {
      key: key,
      value: value,
      type: undefined,
      description: '' + value,
      expanded: this.expanded,
      path: this.prevKey,
    };

    switch (typeof segment.value) {
      case 'number': {
        segment.type = 'number';
        break;
      }
      case 'boolean': {
        segment.type = 'boolean';
        break;
      }
      case 'function': {
        segment.type = 'function';
        break;
      }
      case 'string': {
        segment.type = 'string';
        segment.description = '"' + segment.value + '"';
        break;
      }
      case 'undefined': {
        segment.type = 'undefined';
        segment.description = 'undefined';
        break;
      }
      case 'object': {
        // null is object
        if (segment.value === null) {
          segment.type = 'null';
          segment.description = 'null';
        } else if (Array.isArray(segment.value)) {
          segment.type = 'array';
          segment.description = 'Array[' + segment.value.length + '] ' + JSON.stringify(segment.value);
        } else if (segment.value instanceof Date) {
          segment.type = 'date';
        } else {
          segment.type = 'object';
          segment.description = 'Object ' + JSON.stringify(segment.value);
        }
        break;
      }
    }

    return segment;
  }

  onClick(path, key) {
    this.snackbar.openNotification(
        this.copyTextToClipboard(this.composePath(path, key))
        ? 'JSON path copied to clipboard!'
        : 'Error copying to clipboard'
    );
  }

  composePath(part1: string, part2: any) {
    let isIndex = false;
    if (!isNaN(parseInt(part2, 10))) {
        part2 = '[' + part2 + ']';
        isIndex = true;
    }

    if (isIndex) {
        return part1 + part2;
    } else {
        return part1 + '.' + part2;
    }
  }

  // copies passed text into the user clipboard https://stackoverflow.com/questions/45768583/angular-4-copy-text-to-clipboard
  private copyTextToClipboard(text: string): boolean {
    const textArea = document.createElement('textarea');
    textArea.style.position = 'fixed';
    textArea.style.top = '0';
    textArea.style.left = '0';
    textArea.style.opacity = '0';
    textArea.value = text;
    document.body.appendChild(textArea);
    textArea.select();
    let success;
    try {
        success = document.execCommand('copy');
    } catch (err) {
        console.error('error copying to clipboard, ', err);
    }
    document.body.removeChild(textArea);

    return success;
  }
}